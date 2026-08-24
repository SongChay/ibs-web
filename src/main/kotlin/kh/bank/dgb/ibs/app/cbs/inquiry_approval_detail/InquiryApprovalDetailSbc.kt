package kh.bank.dgb.ibs.app.cbs.inquiry_approval_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

@Service
class InquiryApprovalDetailSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<InquiryApprovalDetailRequest>): ResponseData<InquiryApprovalDetailResponse> {
		val response = connector.post("CIB11303111", request.header?.languageCode, request.body, InquiryApprovalDetailResponse::class.java)
		val info = response.body ?: return response

		val approvalRequestDateTime = combinedDateTime(info.approvalRequestDate, info.approvalRequestTime, ::toDDMMMYYYY, ::toHHMMSSA)
		val scheduleTransferDateTime = combinedDateTime(info.scheduleTransferDate, info.scheduleTransferTime, ::toDDMMMYYYY, ::toHHMMA)

		val enrichedTransferList = info.transferList?.map { item ->
			item.copy(
				transferTypeDescription = transferTypeDescription(item.transferTypeCode),
				transactionTypeDescription = transactionTypeDescription(item.transactionTypeCode),
			)
		}

		val enrichedApprovalStatusList = info.approvalStatusList?.map { item ->
			item.copy(
				approvalDateTime = combinedDateTime(item.approvalDate, item.approvalTime, ::toDDMMMYYYY, ::toHHMMSSA),
				approverTypeDesc = approverTypeDesc(item.approverTypeCode),
				approvalStatusDescription = approvalStatusDescription(item.approvalStatusCode),
			)
		}

		val enrichedApprovalMemoList = info.approvalMemoList?.map { item ->
			item.copy(
				memoDateTime = combinedDateTime(item.memoDate, item.memoTime, ::toDDMMMYYYY, ::toHHMMA),
			)
		}

		val enrichedInfo = info.copy(
			transferTypeDescription = transferTypeDescription(info.transferTypeCode),
			transactionTypeDescription = transactionTypeDescription(info.transactionTypeCode),
			approvalRequestDateTime = approvalRequestDateTime,
			scheduleTransferDateTime = scheduleTransferDateTime,
			transferList = enrichedTransferList,
			approvalStatusList = enrichedApprovalStatusList,
			approvalMemoList = enrichedApprovalMemoList,
		)

		return response.copy(body = enrichedInfo)
	}

	/** Port of the `StringUtils.isNotEmpty(StringUtils.trim(...))` guard used before every
	 *  combined date-time string in the old adapter: only formats when both parts are present,
	 *  otherwise leaves the combined string blank (unlike `InquiryApprovalListSbc`, which had no
	 *  such guard). */
	private fun combinedDateTime(date: String?, time: String?, formatDate: (String?) -> String, formatTime: (String?) -> String): String =
		if (!date.isNullOrBlank() && !time.isNullOrBlank()) formatDate(date) + ", " + formatTime(time) else ""

	/** Port of the old adapter's private inner `TransferTypeCode` enum (`getTransferTypeDescription`). */
	private fun transferTypeDescription(transferTypeCode: String?): String = when (transferTypeCode) {
		"0001" -> "Account"
		"0002" -> "Multi"
		"0003" -> "Account & Wing"
		"0007" -> "Overseas"
		"0008" -> "Wing"
		"0009" -> "Payroll Payment"
		"0013" -> "EDC Payment"
		"0011" -> "EDC Auto Direct Debit Subscribe"
		"0012" -> "EDC Auto Direct Debit Unsubscribe"
		else -> ""
	}

	/** Port of `DataUtils.getTransactionTypeDescription` (backed by `type.TransactionTypeCode`). */
	private fun transactionTypeDescription(transactionTypeCode: String?): String = when (transactionTypeCode) {
		"0001" -> "Immediate"
		"0002" -> "Schedule"
		else -> ""
	}

	/** Port of `DataUtils.getApproverTypeDesc` (backed by `type.ApproverTypeCode`). */
	private fun approverTypeDesc(approverTypeCode: String?): String = when (approverTypeCode) {
		"01" -> "Requestor"
		"02" -> "Approver"
		"03" -> "Final Approver"
		else -> ""
	}

	/** Port of `DataUtils.getApprovalStatusDescription` (backed by `type.ApprovalStatusCodeType`). */
	private fun approvalStatusDescription(approvalStatusCode: String?): String = when (approvalStatusCode) {
		"00" -> "Requested"
		"01" -> "Completed"
		"02" -> "Processing"
		"03" -> "Resubmitted"
		"04" -> "Need My Approval"
		"05" -> "Waiting"
		"08" -> "Rejected"
		"91" -> "Transaction Failed"
		"CC" -> "Canceled"
		else -> ""
	}

	/** Port of `DateUtil.toDDMMMYYYY` — parses the ebanking `yyyyMMdd` date format. */
	private fun toDDMMMYYYY(sDate: String?): String {
		if (sDate.isNullOrBlank()) return ""
		return try {
			val parsed: Date = SimpleDateFormat("yyyyMMdd").parse(sDate)
			SimpleDateFormat("dd MMM yyyy").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}

	/** Port of `DateUtil.toHHMMSSA` — parses the ebanking `HHmmssSSS` time format. */
	private fun toHHMMSSA(sTime: String?): String {
		if (sTime.isNullOrBlank()) return ""
		return try {
			val parsed: Date = SimpleDateFormat("HHmmssSSS").parse(sTime)
			SimpleDateFormat("hh:mm:ss a").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}

	/** Port of `DateUtil.toHHMMA` — takes the first 4 chars (`HHmm`) of the ebanking time format. */
	private fun toHHMMA(sTime: String?): String {
		if (sTime.isNullOrBlank()) return ""
		return try {
			val trimmed = if (sTime.length >= 4) sTime.substring(0, 4) else sTime
			val parsed: Date = SimpleDateFormat("HHmm").parse(trimmed)
			SimpleDateFormat("hh:mm a").format(parsed)
		} catch (e: ParseException) {
			""
		}
	}
}
