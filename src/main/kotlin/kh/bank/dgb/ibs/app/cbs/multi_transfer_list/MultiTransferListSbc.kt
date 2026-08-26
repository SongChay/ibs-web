package kh.bank.dgb.ibs.app.cbs.multi_transfer_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Port of `TRS3101_Adapter_InquiryMultiTransferList#process`. Beyond the plain CBS pass-through,
 * the old adapter combines each item's separate date/time fields into two combined date-time
 * strings using the old `DateUtil` helper (not ported elsewhere, so replicated locally below).
 */
@Service
class MultiTransferListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<MultiTransferListRequest>): ResponseData<MultiTransferListResponse> {
		val response = coreBankingApiConnector.post("CIB11001411", request.header?.languageCode, request.body, MultiTransferListResponse::class.java)
		val enrichedList = response.body?.approvalTransferList?.map { item ->
			val approvalRequestDate = toDDMMMYYYY(item.approvalRequestDate)
			val approvalRequestTime = toHHMMA(item.approvalRequestTime)
			val scheduleTransferDate = toDDMMMYYYY(item.scheduleTransferDate)
			val scheduleTransactionTime = toHHMMA(item.scheduleTransactionTime)
			item.copy(
				approvalRequestDateTime = if (approvalRequestDate.isNotEmpty() && approvalRequestTime.isNotEmpty()) {
					"$approvalRequestDate, $approvalRequestTime"
				} else {
					""
				},
				scheduleTransferDateTime = if (scheduleTransferDate.isNotEmpty() && scheduleTransactionTime.isNotEmpty()) {
					"$scheduleTransferDate, $scheduleTransactionTime"
				} else {
					""
				},
			)
		}
		return response.copy(body = response.body?.copy(approvalTransferList = enrichedList))
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

	private fun toHHMMA(time: String?): String {
		if (time.isNullOrBlank()) return ""
		return try {
			val truncated = if (time.length >= 4) time.substring(0, 4) else time
			val parsed = SimpleDateFormat("HHmm").parse(truncated)
			SimpleDateFormat("hh:mm a").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}
}
