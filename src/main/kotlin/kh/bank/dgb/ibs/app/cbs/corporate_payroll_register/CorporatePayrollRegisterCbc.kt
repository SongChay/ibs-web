package kh.bank.dgb.ibs.app.cbs.corporate_payroll_register

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class CorporatePayrollRegisterItem(
	val phoneNo: String? = null,
	val emailAddress: String? = null,
)

data class CorporatePayrollRegisterRequest(
	val screenNo: String? = null,
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val currencyCode: String? = null,
	val salaryTransferFeeStatusCode: String? = null,
	val salaryTransferFeeAmount: BigDecimal? = null,
	val registerReasonDesc: String? = null,
	val grid01: List<CorporatePayrollRegisterItem>? = null,
)

data class CorporatePayrollRegisterResponse(
	val resultYn: String? = null,
	val message: String? = null,
)

/** Port of `PYR1002_Adapter_RegisterCorporatePayroll` — calls CBS opcode `CIB11300121`. Also
 *  replicates the old adapter's post-processing: overwrites `resultYn` with "Y"/"N" based on
 *  the response header's `result` flag (CBS may already return its own `resultYn`; the old
 *  adapter always clobbers it with the header-derived value). */
@RestController
@RequestMapping("/PYR1002")
class CorporatePayrollRegisterCbc(
	private val sbc: CorporatePayrollRegisterSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<CorporatePayrollRegisterRequest>): ResponseData<CorporatePayrollRegisterResponse> =
		sbc.register(request)
}
