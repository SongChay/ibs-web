package kh.bank.dgb.ibs.app.cbs.corporate_payroll_unsubscribe

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CorporatePayrollUnsubscribeRequest(
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val remark: String? = null,
)

data class CorporatePayrollUnsubscribeResponse(
	val resultYn: String? = null,
	val message: String? = null,
)

/** Port of `PYR1004_Adapter_UnsubscribeCorporatePayroll` — calls CBS opcode `CIB11300123`. Also
 *  replicates the old adapter's post-processing: overwrites `resultYn` with "Y"/"N" based on the
 *  response header's `result` flag. */
@RestController
@RequestMapping("/PYR1004")
class CorporatePayrollUnsubscribeCbc(
	private val sbc: CorporatePayrollUnsubscribeSbc,
) {
	@PostMapping
	fun unsubscribe(@RequestBody request: RequestData<CorporatePayrollUnsubscribeRequest>): ResponseData<CorporatePayrollUnsubscribeResponse> =
		sbc.unsubscribe(request)
}
