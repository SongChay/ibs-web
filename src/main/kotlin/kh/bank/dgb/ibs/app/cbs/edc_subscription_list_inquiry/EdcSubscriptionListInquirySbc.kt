package kh.bank.dgb.ibs.app.cbs.edc_subscription_list_inquiry

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Port of `TRS2514_Adapter_RetrieveSubscriptionListInquiryEDC#process`. Beyond the plain CBS
 * pass-through, the old adapter enriches every item in `grid01` with description/date-time fields
 * derived from the old `DataUtils`/`DateUtil` helpers and a small local enum (not ported elsewhere,
 * so replicated locally below).
 */
@Service
class EdcSubscriptionListInquirySbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<EdcSubscriptionListInquiryRequest>): ResponseData<EdcSubscriptionListInquiryResponse> {
		val response = coreBankingApiConnector.post(
			"CIB11102514",
			request.header?.languageCode,
			request.body,
			EdcSubscriptionListInquiryResponse::class.java,
		)
		val enrichedList = response.body?.grid01?.map { item ->
			val requestDate = toDDMMMYYYY(item.requestDate)
			val requestTime = toHHMMSSA(item.requestTime)
			item.copy(
				transactionStatusCodeDescription = transactionStatusDescription(item.transactionStatusCode),
				transferTypeCodeDescription = "EDC Auto Direct Debit",
				eBankTransactionTypeCodeDescription = eBankTransactionTypeDescription(item.eBankTransactionTypeCode),
				requestDateAndTime = if (requestDate.isNotEmpty() && requestTime.isNotEmpty()) "$requestDate, $requestTime" else "",
			)
		}
		return response.copy(body = response.body?.copy(grid01 = enrichedList))
	}

	private fun transactionStatusDescription(code: String?): String {
		return when (code) {
			"001" -> "Processing"
			"002" -> "Failed"
			"003" -> "Completed"
			"000" -> "Unknown"
			else -> ""
		}
	}

	private fun eBankTransactionTypeDescription(code: String?): String {
		return when (code) {
			"59" -> "EDC Auto Direct Debit Subscribe"
			"60" -> "EDC Auto Direct Debit Unsubscribe"
			else -> ""
		}
	}

	private fun toDDMMMYYYY(date: String?): String {
		if (date.isNullOrBlank()) return ""
		return try {
			val parsed = SimpleDateFormat("yyyyMMdd").parse(date)
			SimpleDateFormat("dd MMM yyyy").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}

	private fun toHHMMSSA(time: String?): String {
		if (time.isNullOrBlank()) return ""
		return try {
			val parsed = SimpleDateFormat("HHmmssSSS").parse(time)
			SimpleDateFormat("hh:mm:ss a").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}
}
