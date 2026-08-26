package kh.bank.dgb.ibs.cbs

import kh.bank.dgb.ibs.cbs.model.CommonBody
import kh.bank.dgb.ibs.cbs.model.CommonHeader
import kh.bank.dgb.ibs.cbs.model.DataHeader
import kh.bank.dgb.ibs.cbs.model.DataList
import kh.bank.dgb.ibs.cbs.model.MaruMessage
import kh.bank.dgb.ibs.cbs.model.MessageData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import java.net.ConnectException
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

/**
 * Port of `DefaultDGBEBankingAPIConnector`.
 *
 * TWO deliberate simplifications from the old code, both flagged rather than silently done:
 *  1. The `transactionSeq` GUID counter was `private static Long transactionSeq` with
 *     `synchronized(transactionSeq)` — synchronizing on a boxed `Long` that gets *reassigned*
 *     every increment, so the lock was never actually protecting anything. Real bug, fixed here
 *     with `AtomicLong`.
 *  2. Error mapping drops the old code's fourth branch (`ConnectionPoolTimeoutException`, a class
 *     specific to Apache HttpClient 4's connection pool). This connector doesn't commit to a
 *     specific backing HTTP client, so only the two JDK-standard exception types
 *     (`ConnectException`, `SocketTimeoutException`) are mapped specifically; anything else
 *     (including whatever pool/lease-timeout exception the actual client throws) falls through to
 *     `UNKNOWN_ERROR`, same as the old code's own final `else` branch.
 *
 * UNVERIFIED: there's no live CBS endpoint reachable from this environment, so the request-
 * building and response-parsing logic below is a faithful reading of the old code, not something
 * that's been exercised against the real system. Test against a real/sandbox CBS endpoint before
 * this goes anywhere near production traffic.
 */
@Component
class DefaultCoreBankingApiConnector(
	private val restClient: RestClient,
	private val objectMapper: ObjectMapper,
	private val coreBankingProperties: CoreBankingProperties,
) : CoreBankingApiConnector {

	private val logger = LoggerFactory.getLogger(DefaultCoreBankingApiConnector::class.java)
	private val transactionSeq = AtomicLong(0)

	override fun <T1, T2> post(operationCode: String, languageCode: String?, requestBody: T1, responseBodyType: Class<T2>): ResponseData<T2> {
		val message = buildMaruMessage(operationCode, languageCode, requestBody)

		logger.info("Calling core banking: op={}, request={}", operationCode, requestBody)
		val startedAt = System.currentTimeMillis()

		return try {
			val responseNode = restClient.post()
				.uri(coreBankingProperties.baseUrl)
				.body(message)
				.retrieve()
				.body(ObjectNode::class.java)
				?: return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.CBK_NO_RESPONSE_HEADER_EB))

			parseResponse(responseNode, responseBodyType)
		} catch (e: RestClientException) {
			logger.error("Core banking call failed: op={}, request={}", operationCode, requestBody, e)
			ResponseData(header = ResponseResultUtils.makeResponse(false, classifyError(e)))
		} finally {
			logger.info("Core banking call finished: op={}, elapsedMs={}", operationCode, System.currentTimeMillis() - startedAt)
		}
	}

	private fun <T2> parseResponse(responseNode: ObjectNode, responseBodyType: Class<T2>): ResponseData<T2> {
		val msgBodyNode = responseNode.get("msgBody")
		val commonBodyNode = responseNode.get("commonBody")
		val dataBodyNode = responseNode.get("msgData")?.get("dataList")?.findValue("dataBody")

		val outMsgCd = msgBodyNode?.get("outMsgCd")?.asString() ?: ""
		val outMsgDesc = msgBodyNode?.get("outMsgDesc")?.asString() ?: ""
		val trxDate = commonBodyNode?.get("trxDate")?.asString()

		val header = ResponseUserHeaderVo(
			result = outMsgCd == "100000",
			resultCode = outMsgCd,
			resultMessage = "$outMsgDesc ($outMsgCd)",
			transactionID = trxDate,
			transactionDate = trxDate,
		)

		val body = dataBodyNode?.takeUnless { it.isNull }?.let { objectMapper.convertValue(it, responseBodyType) }
		return ResponseData(header = header, body = body)
	}

	private fun classifyError(e: RestClientException): ResponseResultCodeType {
		val rootCause = generateSequence<Throwable>(e) { it.cause }.last()
		return when (rootCause) {
			is ConnectException -> ResponseResultCodeType.CBK_ERROR_CONNECT_EXCEPTION
			is SocketTimeoutException -> ResponseResultCodeType.CBK_ERROR_SOCKET_TIMEOUT_EXCEPTION
			else -> ResponseResultCodeType.UNKNOWN_ERROR
		}
	}

	private fun <T1> buildMaruMessage(operationCode: String, languageCode: String?, requestBody: T1): MaruMessage<T1> {
		val now = Date()
		val commonHdr = CommonHeader(
			encProcTypeCd = "0",
			firstIPAddr = localIpAddress(),
			firstReqSysCd = coreBankingProperties.systemCode,
			guid = generateGuid(),
			msgReqResTypeCd = "S",
			msgVerNo = coreBankingProperties.messageVersionNumber,
			totTimeoutSec = coreBankingProperties.totalTimeoutSeconds,
			trxTimeoutSec = coreBankingProperties.transactionTimeoutSeconds,
		)

		val commonBody = CommonBody(
			ctryCd = coreBankingProperties.countryCode,
			compIdCd = coreBankingProperties.companyIdCode,
			svcID = operationCode,
			ifID = operationCode + coreBankingProperties.systemCode,
			trxSyncTypeCd = coreBankingProperties.transactionSyncTypeCode,
			inExTypeCd = "1",
			sysEnvTypeCd = coreBankingProperties.systemEnvironmentTypeCode,
			trxProcTypeCd = coreBankingProperties.transactionProcessTypeCode,
			orgTrxRestYN = coreBankingProperties.originalTransactionRestoreYn,
			bankCd = coreBankingProperties.bankCode,
			trxBrchCd = coreBankingProperties.transactionBranchCode,
			outBrchTypeCd = "00",
			actTrxBrchCd = coreBankingProperties.actualTransactionBranchCode,
			chnTypeCd = coreBankingProperties.channelTypeCode,
			chnDetTypeCd = coreBankingProperties.channelDetailTypeCode,
			trmnNo = coreBankingProperties.terminalNumber,
			langTypeCd = resolveLanguage(languageCode),
			mgrApprSeqNo = 0L,
			tellerID = coreBankingProperties.tellerId,
			msgReqDate = SimpleDateFormat("yyyyMMdd").format(now),
			msgReqTime = SimpleDateFormat("HHmmssSSS").format(now),
		)

		return MaruMessage(
			commonHdr = commonHdr,
			commonBody = commonBody,
			msgData = MessageData(
				dataHdr = DataHeader(dataHdrTypeCd = "IO"),
				dataList = listOf(DataList(dataBody = requestBody)),
			),
		)
	}

	/** Port of the old `lang.matches("km|02|KM") ? "KM" : "EN"` logic — that version NPE'd if
	 *  `languageCode` was null before the null-check further down ever ran; this doesn't. */
	private fun resolveLanguage(languageCode: String?): String {
		return when {
			languageCode == null -> coreBankingProperties.defaultLanguageTypeCode
			languageCode == "km" || languageCode == "02" || languageCode == "KM" -> "KM"
			else -> "EN"
		}
	}

	private fun localIpAddress(): String {
		return runCatching { InetAddress.getLocalHost().hostAddress }.getOrDefault("")
	}

	/** Port of `genGuid()`. */
	private fun generateGuid(): String {
		val dateTime = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
		val seq = transactionSeq.updateAndGet { current -> if (current >= 999999) 1 else current + 1 }
		val seqPadded = seq.toString().padStart(6, '0')
		return "${coreBankingProperties.companyIdCode}${coreBankingProperties.systemCode}${coreBankingProperties.tellerId}0$dateTime$seqPadded" + "001"
	}
}
