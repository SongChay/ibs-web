package kh.bank.dgb.ibs.app.cbs.corporate_payroll_registration_check

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CorporatePayrollRegistrationCheckRequest(
	val customerNo: String? = null,
)

data class CorporatePayrollRegistrationCheckItem(
	val salaryTransferAccountNo: String? = null,
)

data class CorporatePayrollRegistrationCheckResponse(
	val register: Boolean? = null,
	val grid01Count: Int? = null,
	val grid01: List<CorporatePayrollRegistrationCheckItem>? = null,
)

/** Port of `PYR1003_Adapter_CheckRegisterCorporatePayroll` — calls CBS opcode `CIB11300122`.
 *  Also replicates the old adapter's post-processing: overwrites `register` with `true`/`false`
 *  based on whether `grid01` came back non-empty (the old code ignores whatever `register` value
 *  CBS itself may have returned). */
@RestController
@RequestMapping("/PYR1003")
class CorporatePayrollRegistrationCheckCbc(
	private val sbc: CorporatePayrollRegistrationCheckSbc,
) {
	@PostMapping
	fun check(@RequestBody request: RequestData<CorporatePayrollRegistrationCheckRequest>): ResponseData<CorporatePayrollRegistrationCheckResponse> =
		sbc.check(request)
}
