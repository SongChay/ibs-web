package kh.bank.dgb.ibs.app.cbs.inquiry_approval_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class InquiryApprovalListRequest(
	val userID: String? = null,
	/** 00 Requested, 01 Approved, 02 Approving, 03 Resubmitted, 08 Rejected, 91 Transaction Failed */
	val approvalTypeCode: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	/** blank/null = ALL, 00 Requested, 01 Approved, 02 Approving, 03 Resubmitted, 08 Rejected, CC Canceled */
	val approvalStatusCode: String? = null,
	val pageSize: Int = 0,
	val currentPage: Int = 0,
)

data class ApprovalListItem(
	val approvalNo: Long = 0,
	val transferTypeCode: String? = null,
	val transferTypeDescription: String? = null,
	val transactionTypeCode: String? = null,
	val transactionTypeDescription: String? = null,
	val withdrawalAccountNo: String? = null,
	val requester: String? = null,
	val approvalRequestDate: String? = null,
	val approvalRequestTime: String? = null,
	val approvalRequestDateTime: String? = null,
	val approvalStatusCode: String? = null,
	val approvalStatusDescription: String? = null,
	val transferTotalAmount: BigDecimal? = null,
	val transferTotalFee: BigDecimal? = null,
	val transactionCurrencyCode: String? = null,
	val transferTotalCount: Int = 0,
)

/** Port of `APV1001_RES_WrapperInquiryApprovalListVo` — `totalCount`/`approvalList` are wire-named
 *  `grid01Count`/`grid01` (field-level `@JsonProperty` in the old Vo, symmetric in both
 *  directions). */
data class InquiryApprovalListResponse(
	@JsonProperty("grid01Count") val totalCount: Long = 0,
	@JsonProperty("grid01") val approvalList: List<ApprovalListItem>? = null,
)

/** Port of `APV1001_Adapter_InquiryApprovalList` — calls CBS opcode `CIB11303011` (via the old
 *  `DGBEBankingService.processAPR0002`).
 *
 *  Real logic beyond a pass-through, replicated in the Sbc: (1) an empty `approvalStatusCode` is
 *  normalized to `null` before the call, (2) each returned row is enriched with
 *  `transferTypeDescription`/`transactionTypeDescription`/`approvalStatusDescription` lookups and
 *  a combined `approvalRequestDateTime` string, all computed from fixed code tables that used to
 *  live in `TransactionTypeCode` (inner enum of the old adapter), `DataUtils`, and `DateUtil`. */
@RestController
@RequestMapping("/APV1001")
class InquiryApprovalListCbc(
	private val sbc: InquiryApprovalListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<InquiryApprovalListRequest>): ResponseData<InquiryApprovalListResponse> =
		sbc.inquire(request)
}
