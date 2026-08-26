package kh.bank.dgb.ibs.app.cbs.payroll_payment_schedule_detail

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentScheduleDetailRequest(
	val customerNo: String? = null,
	val salaryTransferExecutionDate: String? = null,
	val acceptanceSeqNo: Long? = null,
	val salaryTransferAccountNo: String? = null,
)

data class PayrollPaymentScheduleDetailItem(
	val receiptAccountNo: String? = null,
	val currencyCode: String? = null,
	val receiptAmount: BigDecimal? = null,
	val accountName: String? = null,
	val depositSubjectCode: String? = null,
	val depositAccountStatusCode: String? = null,
	val salaryTransferStatusCode: String? = null,
	val salaryTransferErrorCode: String? = null,
	val salaryTransferErrorName: String? = null,
	val jobSeqNo: Long? = null,
	val remark: String? = null,
)

/** Old Vo (`PYR1102_RES_WrapperRetrivePayrollPaymentScheduleDetailVo`) deserializes the list from
 *  CBS's `grid01` but re-exposes it to the client as `payrollPaymentScheduleDetail`
 *  (`@JsonGetter("payrollPaymentScheduleDetail")` / `@JsonSetter("grid01")`). Replicated here. */
data class PayrollPaymentScheduleDetailResponse(
	val grid02Count: Long? = null,
	@param:JsonProperty("grid01")
	@get:JsonProperty("payrollPaymentScheduleDetail")
	val payrollPaymentScheduleDetail: List<PayrollPaymentScheduleDetailItem>? = null,
)

/** Port of `PYR1102_Adapter_RetrivePayrollPaymentScheduleDetail` — calls CBS opcode `CIB11300212`
 *  and returns the result as-is (no post-processing in the old adapter). */
@RestController
@RequestMapping("/PYR1102")
class PayrollPaymentScheduleDetailCbc(
	private val payrollPaymentScheduleDetailSbc: PayrollPaymentScheduleDetailSbc,
) {
	@PostMapping
	fun detail(@RequestBody request: RequestData<PayrollPaymentScheduleDetailRequest>): ResponseData<PayrollPaymentScheduleDetailResponse> {
		return payrollPaymentScheduleDetailSbc.detail(request)
	}
}
