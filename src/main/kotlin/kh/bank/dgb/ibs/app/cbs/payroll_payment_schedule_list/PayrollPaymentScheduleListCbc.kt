package kh.bank.dgb.ibs.app.cbs.payroll_payment_schedule_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentScheduleListRequest(
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferExecutionDate: String? = null,
)

data class PayrollPaymentScheduleItem(
	val salaryTransferExecutionDate: String? = null,
	val salaryTransferExecutionTime: String? = null,
	val salaryTransferStatusCode: String? = null,
	val salaryTransferCount: BigDecimal? = null,
	val salaryTransferAmount: BigDecimal? = null,
	val salaryTransferTotalFeeAmount: BigDecimal? = null,
	val remark: String? = null,
	val acceptanceSeqNo: Long? = null,
	val scheduleTransferTypeCode: String? = null,
)

/** Old Vo (`PYR1101_RES_WrapperRetrieveListPayrollPaymentScheduleVo`) deserializes the list from
 *  CBS's `grid01` but re-exposes it to the client as `payrollPaymentScheduleList`
 *  (`@JsonGetter("payrollPaymentScheduleList")` / `@JsonSetter("grid01")`). Replicated here. */
data class PayrollPaymentScheduleListResponse(
	val salaryRegisterStatusCode: String? = null,
	val registerDate: String? = null,
	val unregisterDate: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferFeeStatusCode: String? = null,
	val salaryTransferFeeAmount: BigDecimal? = null,
	val currencyCode: String? = null,
	val grid01Count: String? = null,
	@param:JsonProperty("grid01")
	@get:JsonProperty("payrollPaymentScheduleList")
	val payrollPaymentScheduleList: List<PayrollPaymentScheduleItem>? = null,
)

/** Port of `PYR1101_Adapter_RetrieveListPayrollPaymentSchedule` — calls CBS opcode `CIB11300211`.
 *  Also replicates the old adapter's post-processing (only when the response header's `result`
 *  flag is true): rescales `salaryTransferFeeAmount` (top-level) and each item's
 *  `salaryTransferAmount`/`salaryTransferTotalFeeAmount` to 2 decimal places, and formats
 *  `registerDate`/`unregisterDate`/item `salaryTransferExecutionDate` from `yyyyMMdd` to
 *  `dd MMM yyyy`. */
@RestController
@RequestMapping("/PYR1101")
class PayrollPaymentScheduleListCbc(
	private val payrollPaymentScheduleListSbc: PayrollPaymentScheduleListSbc,
) {
	@PostMapping
	fun list(@RequestBody request: RequestData<PayrollPaymentScheduleListRequest>): ResponseData<PayrollPaymentScheduleListResponse> {
		return payrollPaymentScheduleListSbc.list(request)
	}
}
