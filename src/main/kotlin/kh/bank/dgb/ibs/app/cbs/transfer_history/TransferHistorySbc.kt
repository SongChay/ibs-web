package kh.bank.dgb.ibs.app.cbs.transfer_history

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Port of `TRS3003_Adapter_InquiryTransferHistory#process`. Beyond the plain CBS pass-through, the
 * old adapter enriches every item in the result list with description/date-time fields derived
 * from the old `DataUtils`/`DateUtil` helpers and a small local enum (not ported elsewhere, so
 * replicated locally below).
 */
@Service
class TransferHistorySbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<TransferHistoryRequest>): ResponseData<TransferHistoryResponse> {
		val response = coreBankingApiConnector.post("CIB11301311", request.header?.languageCode, request.body, TransferHistoryResponse::class.java)
		val enrichedList = response.body?.transferList?.map { item ->
			val transactionDate = toDDMMMYYYY(item.transactionDate)
			val transactionTime = toHHMMSSA(item.transactionTime)
			item.copy(
				transactionStatusDescription = transactionStatusDescription(item.transactionStatusCode),
				transferTypeDescription = transferTypeDescription(item.transferTypeCode),
				transactionTypeDescription = transactionTypeDescription(item.transactionTypeCode),
				requestDate = toDDMMMYYYY(item.requestDate),
				transactionDateTime = if (transactionDate.isNotEmpty() && transactionTime.isNotEmpty()) "$transactionDate, $transactionTime" else "",
			)
		}
		return response.copy(body = response.body?.copy(transferList = enrichedList))
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

	private fun transferTypeDescription(code: String?): String {
		return when (code) {
			"0001" -> "Domestic"
			"0007" -> "Overseas"
			"0008" -> "Wing"
			else -> ""
		}
	}

	private fun transactionTypeDescription(code: String?): String {
		return when (code) {
			"0001" -> "Immediate"
			"0002" -> "Schedule"
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
