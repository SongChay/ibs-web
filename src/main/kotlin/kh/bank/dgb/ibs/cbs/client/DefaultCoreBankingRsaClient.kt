package kh.bank.dgb.ibs.cbs.client

import kh.bank.dgb.ibs.cbs.properties.CoreBankingRsaProperties
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.RequestUserHeaderVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import tools.jackson.databind.node.ObjectNode

/**
 * Port of the RSA sub-call inside the old `AuthenticationProviderImpl.authenticate` — a plain
 * REST POST to CBS's RSA endpoint. Response is FLAT JSON (`resultCode`/`publicKeyModulus`/
 * `publicKeyExponent` at the root), unlike every other CBS call, which goes through the full Maru
 * envelope via `CoreBankingApiConnector` — kept as its own tiny client for that reason rather than
 * forced through the generic connector.
 *
 * UNVERIFIED like every other CBS integration point in this app — no live CBS reachable from this
 * environment.
 */
@Component
class DefaultCoreBankingRsaClient(
	private val restClient: RestClient,
	private val coreBankingRsaProperties: CoreBankingRsaProperties,
) : CoreBankingRsaClient {

	private val logger = LoggerFactory.getLogger(DefaultCoreBankingRsaClient::class.java)

	override fun requestPublicKey(header: RequestUserHeaderVo): ChannelRsaKeyResult? {
		val request = RequestData(header = header, body = TerminalRequest(terminalUniqueNo = coreBankingRsaProperties.terminalUniqueNo))

		return try {
			val response = restClient.post()
				.uri(coreBankingRsaProperties.url)
				.body(request)
				.retrieve()
				.body(ObjectNode::class.java)
				?: return null

			val resultCode = response.get("resultCode")?.asString()
			if (resultCode != SUCCESS_RESULT_CODE) {
				logger.warn("CBS RSA handshake failed: resultCode={}", resultCode)
				return null
			}

			val modulus = response.get("publicKeyModulus")?.asString()
			val exponent = response.get("publicKeyExponent")?.asString()
			if (modulus == null || exponent == null) null else ChannelRsaKeyResult(modulus, exponent)
		} catch (e: RestClientException) {
			logger.error("CBS RSA handshake call failed", e)
			null
		}
	}

	/** Single-use request-body DTO for this call only — nested here rather than a standalone file,
	 *  same rationale as `DecryptedBodyRequestWrapper` nested in `EncryptedEnvelopeFilter`. */
	private data class TerminalRequest(val terminalUniqueNo: String? = null)

	companion object {
		private const val SUCCESS_RESULT_CODE = "00000"
	}
}
