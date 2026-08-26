package kh.bank.dgb.ibs.app.cbs.payroll_payment_detail

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentDetailRequest(
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferExecutionDate: String? = null,
	val acceptanceSeqNo: Long? = null,
	val approvalNo: Long? = null,
	val pageSize: Long? = null,
	val pageNumber: String? = null,
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

data class PayrollPaymentDetailItem(
	val jobSeqNo: Long? = null,
	val receiptAccountNo: String? = null,
	val accountName: String? = null,
	val receiptAmount: BigDecimal? = null,
	val feeAmount: BigDecimal? = null,
	val currencyCode: String? = null,
	val salaryTransferStatusCode: String? = null,
	val remark: String? = null,
	val receiverBankName: String? = null,
	val transactionStatus: String? = null,
	val transactionStatusDesc: String? = null,
	val error: String? = null,
)

/** Old `PYR1202_RES_WrapperRetrievePayrollPaymentDetailVo`: `retrievePayrollPaymentDetail`
 *  deserializes from CBS's `grid02`, `approvalStatusList` from `grid03` (both
 *  `@JsonGetter`/`@JsonSetter` pairs, `grid02Count` is symmetric). Replicated. */
data class PayrollPaymentDetailGroup(
	val grid02Count: Long? = null,
	@param:JsonProperty("grid02")
	@get:JsonProperty("retrievePayrollPaymentDetail")
	val retrievePayrollPaymentDetail: List<PayrollPaymentDetailItem>? = null,
	@param:JsonProperty("grid03")
	@get:JsonProperty("approvalStatusList")
	val approvalStatusList: List<ApprovalStatusItem>? = null,
)

/** Old `PYR1202_RES_Wrapper1RetrievePayrollPaymentDetailVo`: `wrapperRetrievePayrollPaymentDetail`
 *  deserializes from CBS's `grid01` (`grid01Count` is symmetric). Replicated. */
data class PayrollPaymentDetailResponse(
	val grid01Count: Long? = null,
	@param:JsonProperty("grid01")
	@get:JsonProperty("wrapperRetrievePayrollPaymentDetail")
	val wrapperRetrievePayrollPaymentDetail: PayrollPaymentDetailGroup? = null,
)

/** Port of `PYR1202_Adapter_RetrievePayrollPaymentDetail` — calls CBS opcode `CIB11300912`. Also
 *  replicates the old adapter's per-item post-processing: rescales `receiptAmount`/`feeAmount` to
 *  2 decimal places and fills `transactionStatusDesc` from `transactionStatus` via the old
 *  `DataUtils.getTransactionStatusDescription` lookup (`001`=Processing, `002`=Failed,
 *  `003`=Completed, `000`=Unknown, else ""). */
@RestController
@RequestMapping("/PYR1202")
class PayrollPaymentDetailCbc(
	private val payrollPaymentDetailSbc: PayrollPaymentDetailSbc,
) {
	@PostMapping
	fun detail(@RequestBody request: RequestData<PayrollPaymentDetailRequest>): ResponseData<PayrollPaymentDetailResponse> {
		return payrollPaymentDetailSbc.detail(request)
	}
}
