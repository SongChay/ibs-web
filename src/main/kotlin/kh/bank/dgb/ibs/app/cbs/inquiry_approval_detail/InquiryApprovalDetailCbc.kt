package kh.bank.dgb.ibs.app.cbs.inquiry_approval_detail

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class InquiryApprovalDetailRequest(
	val userID: String? = null,
	val approvalNo: Long = 0,
)

data class TransferDetailItem(
	val seqNo: Long = 0,
	val transferTypeCode: String? = null,
	val transferTypeDescription: String? = null,
	val transactionTypeCode: String? = null,
	val transactionTypeDescription: String? = null,
	val approvalRequestDate: String? = null,
	val approvalRequestTime: String? = null,
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val receiverBankCode: String? = null,
	val transactionStatusCode: String? = null,
	val senderAddress: String? = null,
	val recipientBankSwiftCode: String? = null,
	val recipientAccountNo: String? = null,
	val recipientAddress: String? = null,
	val purposeTransfer: String? = null,
	val receiverName: String? = null,
	val transactionAmount: BigDecimal? = null,
	val transactionFeeAmount: BigDecimal? = null,
	val receiverAccountNo: String? = null,
	val counterpartBankAccountNumber: String? = null,
	val receiverAccountRemark: String? = null,
	val receiverBankName: String? = null,
	val receiverPhoneNo: String? = null,
	val transactionCurrencyCode: String? = null,
	val receiverCountryCode: String? = null,
	val receiverCountryName: String? = null,
	val transactionErrorDetailCode: String? = null,
	val purposeOfRequestDescription: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class ApprovalStatusItem(
	val approvalNo: Long = 0,
	val approvalStepNo: Long = 0,
	val approverID: String? = null,
	val approverName: String? = null,
	val approverTypeCode: String? = null,
	val approverTypeDesc: String? = null,
	/** 00 Requested, 01 Approved(Completed), 02 Approving(Waiting), 03 Resubmitted, 08 Rejected, 91 Transaction Failed */
	val approvalStatusCode: String? = null,
	val approvalStatusDescription: String? = null,
	val approvalDate: String? = null,
	val approvalTime: String? = null,
	val approvalDateTime: String? = null,
)

/** Port of `APV1101_RES_ApprovalHistoryListVo` — `memoDateTime` was `@JsonIgnore` in the old Vo
 *  (computed for internal use only, never surfaced to the client); kept ignored here. */
data class ApprovalMemoItem(
	val seqNo: Long = 0,
	val userID: String? = null,
	val userName: String? = null,
	val memoDate: String? = null,
	val memoTime: String? = null,
	@get:JsonIgnore val memoDateTime: String? = null,
	val approvalMemo: String? = null,
	val approvalNo: Long = 0,
)

/** Port of `APV1101_RES_WrapperInquiryApprovalDetailVo` — `transferList`/`approvalStatusList`/
 *  `approvalMemoList` used asymmetric `@JsonGetter`/`@JsonSetter` pairs in the old Vo: CBS returns
 *  them as `grid01`/`grid02`/`grid03`, but they're re-exposed to the client under their plain
 *  names. Kept exactly: `@param` controls what key is read from CBS's response, `@get` controls
 *  what key is sent to the client. */
data class InquiryApprovalDetailResponse(
	val withdrawalAccountNo: String? = null,
	val accountNickName: String? = null,
	val transferTypeCode: String? = null,
	val transferTypeDescription: String? = null,
	val transactionTypeCode: String? = null,
	val transactionTypeDescription: String? = null,
	val approvalRequestDate: String? = null,
	val approvalRequestTime: String? = null,
	val approvalRequestDateTime: String? = null,
	val scheduleTransferDate: String? = null,
	val scheduleTransferTime: String? = null,
	val scheduleTransferDateTime: String? = null,
	val transferTotalCount: Int = 0,
	val transferTotalAmount: BigDecimal? = null,
	val transferTotalFee: BigDecimal? = null,
	val otherMemo: String? = null,
	@param:JsonProperty("grid01") @get:JsonProperty("transferList")
	val transferList: List<TransferDetailItem>? = null,
	@param:JsonProperty("grid02") @get:JsonProperty("approvalStatusList")
	val approvalStatusList: List<ApprovalStatusItem>? = null,
	@param:JsonProperty("grid03") @get:JsonProperty("approvalMemoList")
	val approvalMemoList: List<ApprovalMemoItem>? = null,
)

/** Port of `APV1101_Adapter_InquiryApprovalDetail` — calls CBS opcode `CIB11303111` (via the old
 *  `DGBEBankingService.processAPR0003`).
 *
 *  Real logic beyond a pass-through, replicated in the Sbc: the top-level info, each transfer-list
 *  row, and each approval-status-list row are enriched with `transferTypeDescription`/
 *  `transactionTypeDescription`/`approverTypeDesc`/`approvalStatusDescription` lookups and
 *  combined date-time strings, all computed from fixed code tables that used to live in
 *  `TransferTypeCode` (inner enum of the old adapter), `DataUtils`, and `DateUtil`. */
@RestController
@RequestMapping("/APV1101")
class InquiryApprovalDetailCbc(
	private val sbc: InquiryApprovalDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<InquiryApprovalDetailRequest>): ResponseData<InquiryApprovalDetailResponse> =
		sbc.inquire(request)
}
