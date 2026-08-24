package kh.bank.dgb.ibs.app.cbs.payroll_payment_cancel

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PayrollPaymentCancelRequest(
	val salaryTransferExecutionDate: String? = null,
	val customerNo: String? = null,
	val acceptanceSeqNo: Long? = null,
)

data class PayrollPaymentCancelResponse(
	val resultYn: String? = null,
)

/**
 * Port of `PYR1104_Adapter_CancelPayrollPayment` — calls CBS opcode `CIB11300232` (see TODO
 * below) and overwrites `resultYn` with "Y"/"N" based on the response header's `result` flag.
 *
 * TODO / FLAGGED FOR REVIEW: the old adapter calls `service.processPYR05300232(requestVO)`, but
 * that method does not exist anywhere in `DGBEBankingService` / `DGBEBankingServiceImpl` — it's
 * neither declared in the interface nor implemented (grepped the whole old source tree; the only
 * hit is the one call site in the old adapter itself). This adapter would not have compiled in
 * the old app, so its real opcode can't be recovered from source. Following the established
 * "process<OPCODE>" naming convention used by every other adapter in this batch, the literal
 * opcode implied by the method name would be `"PYR05300232"` (not the `CIB113002xx` family the
 * sibling PYR11xx adapters use) — used here as the best-effort guess, but this is unverified and
 * needs confirmation against the real CBS opcode catalog before this goes near production.
 */
@RestController
@RequestMapping("/PYR1104")
class PayrollPaymentCancelCbc(
	private val sbc: PayrollPaymentCancelSbc,
) {
	@PostMapping
	fun cancel(@RequestBody request: RequestData<PayrollPaymentCancelRequest>): ResponseData<PayrollPaymentCancelResponse> =
		sbc.cancel(request)
}
