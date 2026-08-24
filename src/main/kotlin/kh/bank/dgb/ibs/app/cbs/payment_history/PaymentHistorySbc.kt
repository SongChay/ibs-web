package kh.bank.dgb.ibs.app.cbs.payment_history

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Port of `TRS1312_Adapter_InquiryPaymentHistory#process`. Beyond the plain CBS pass-through, the
 * old adapter enriches every item in the result list with description/date-time fields derived
 * from the old `DataUtils`/`DateUtil` helpers (not ported elsewhere, so replicated locally below).
 *
 * NOTE: the old code sets `transferTypeDescription` to the constant "Domestic" for every item,
 * regardless of the item's actual `transferTypeCode` (see `TRS1312_Adapter_InquiryPaymentHistory
 * .java:39`) — this looks like a latent bug in the old adapter, but is replicated faithfully here
 * per the porting instructions. Flagged for review.
 */
@Service
class PaymentHistorySbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<PaymentHistoryRequest>): ResponseData<PaymentHistoryResponse> {
		val response = connector.post("CIB11301312", request.header?.languageCode, request.body, PaymentHistoryResponse::class.java)
		val enrichedList = response.body?.paymentList?.map { item ->
			val transactionDate = toDDMMMYYYY(item.transactionDate)
			val transactionTime = toHHMMSSA(item.transactionTime)
			item.copy(
				transactionStatusDescription = transactionStatusDescription(item.transactionStatusCode),
				transferTypeDescription = "Domestic",
				transactionTypeDescription = transactionTypeDescription(item.transactionTypeCode),
				requestDate = toDDMMMYYYY(item.requestDate),
				transactionDateTime = if (transactionDate.isNotEmpty() && transactionTime.isNotEmpty()) "$transactionDate, $transactionTime" else "",
			)
		}
		return response.copy(body = response.body?.copy(paymentList = enrichedList))
	}

	private fun transactionStatusDescription(code: String?): String = when (code) {
		"001" -> "Processing"
		"002" -> "Failed"
		"003" -> "Completed"
		"000" -> "Unknown"
		else -> ""
	}

	private fun transactionTypeDescription(code: String?): String = when (code) {
		"0001" -> "Immediate"
		"0002" -> "Schedule"
		else -> ""
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
