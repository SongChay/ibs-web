package kh.bank.dgb.ibs.app.cbs.inquiry_approval_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

@Service
class InquiryApprovalListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<InquiryApprovalListRequest>): ResponseData<InquiryApprovalListResponse> {
		// Port of: if (StringUtils.isEmpty(approvalStatusCode)) approvalStatusCode = null;
		val body = request.body?.let {
			if (it.approvalStatusCode.isNullOrEmpty()) it.copy(approvalStatusCode = null) else it
		}

		val response = connector.post("CIB11303011", request.header?.languageCode, body, InquiryApprovalListResponse::class.java)

		val enrichedList = response.body?.approvalList?.map { item ->
			item.copy(
				transferTypeDescription = transferTypeDescription(item.transferTypeCode),
				transactionTypeDescription = transactionTypeDescription(item.transactionTypeCode),
				approvalStatusDescription = approvalStatusDescription(item.approvalStatusCode),
				approvalRequestDateTime = toDDMMMYYYY(item.approvalRequestDate) + ", " + toHHMMSSA(item.approvalRequestTime),
			)
		}

		return if (enrichedList != null) response.copy(body = response.body?.copy(approvalList = enrichedList)) else response
	}

	/** Port of the old adapter's private inner `TransactionTypeCode` enum (`getTransferTypeDescription`). */
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
}
