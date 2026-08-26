package kh.bank.dgb.ibs.app.cbs.payroll_payment_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentListRequest(
	val branchCode: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val sortBy: String? = null,
	val inquireBy: String? = null,
	val pageSize: Long? = null,
	val pageNumber: String? = null,
	val salaryTransferAccountNo: String? = null,
)

/** Old `APV1101_RES_ApprovalStatusListVo` — field names/casing kept exactly. */
data class ApprovalStatusItem(
	val approvalNo: Long? = null,
	val approvalStepNo: Long? = null,
	val approverID: String? = null,
	val approverName: String? = null,
	val approverTypeCode: String? = null,
	val approverTypeDesc: String? = null,
	val approvalStatusCode: String? = null,
	val approvalStatusDescription: String? = null,
	val approvalDate: String? = null,
	val approvalTime: String? = null,
	val approvalDateTime: String? = null,
)

/** Old `PYR1201_RES_RetrieveListPayrollPaymentVo` re-exposes its approval list under
 *  `approvalList` to the client, but deserializes it from CBS's `grid02`
 *  (`@JsonGetter("approvalList")` / `@JsonSetter("grid02")`). Replicated here. */
data class PayrollPaymentListItem(
	val approvalNo: Long? = null,
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferExecutionDate: String? = null,
	val salaryTransferExecutionTime: String? = null,
	val firstRegisterDate: String? = null,
	val firstRegisterTime: String? = null,
	val updateDate: String? = null,
	val updateTime: String? = null,
	val salaryTransferAmount: BigDecimal? = null,
	val feeAmount: BigDecimal? = null,
	val salaryTransferCount: Int? = null,
	val totalCompleted: Int? = null,
	val totalProcessing: Int? = null,
	val totalFailed: Int? = null,
	val currencyCode: String? = null,
	val remark: String? = null,
	val transferStatus: String? = null,
	val transferStatusCode: String? = null,
	val canCancelAndModify: Boolean? = null,
	val status: String? = null,
	val acceptanceSeqNo: String? = null,
	@param:JsonProperty("grid02")
	@get:JsonProperty("approvalList")
	val approvalList: List<ApprovalStatusItem>? = null,
)

/** Old `PYR1201_RES_WrapperRetrieveListPayrollPaymentVo`: `totalCount` deserializes from CBS's
 *  `grid01Count`, the item list from `grid01` (`@JsonGetter`/`@JsonSetter` pairs). Replicated. */
data class PayrollPaymentListResponse(
	@param:JsonProperty("grid01Count")
	@get:JsonProperty("totalCount")
	val totalCount: Long? = null,
	@param:JsonProperty("grid01")
	@get:JsonProperty("payrollPaymentList")
	val payrollPaymentList: List<PayrollPaymentListItem>? = null,
)

/** Port of `PYR1201_Adapter_RetrieveListPayrollPayment` — calls CBS opcode `CIB11300911`. Also
 *  replicates the old adapter's per-item post-processing: rescales `salaryTransferAmount`/
 *  `feeAmount` to 2 decimal places, and formats `firstRegisterDate`/`salaryTransferExecutionDate`/
 *  `updateDate` (`yyyyMMdd` -> `dd MMM yyyy`) and `firstRegisterTime`/`updateTime`
 *  (first 4 chars as `HHmm` -> `hh:mm a`). The old code's `salaryTransferFeeStatusCode` ->
 *  `salaryTransferFeeStatusText` map is commented-out dead code in the old adapter and is
 *  intentionally not ported. */
@RestController
@RequestMapping("/PYR1201")
class PayrollPaymentListCbc(
	private val payrollPaymentListSbc: PayrollPaymentListSbc,
) {
	@PostMapping
	fun list(@RequestBody request: RequestData<PayrollPaymentListRequest>): ResponseData<PayrollPaymentListResponse> {
		return payrollPaymentListSbc.list(request)
	}
}
