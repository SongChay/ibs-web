package kh.bank.dgb.ibs.app.cbs.oversea_transfer_fee

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class OverseaTransferFeeRequest(
	val feeTypeCode: Int? = null,
	val currencyCode: String? = null,
	val amount: BigDecimal? = null,
	val overseaCommissionTypeCode: String? = null,
)

data class OverseaTransferFeeResponse(
	val inBoundFee: Double? = null,
	val outBoundFee: Double? = null,
	val cableFee: Double? = null,
	val resultYn: String? = null,
	val feeCurrencyCode: String? = null,
	val transferFee: BigDecimal? = null,
)

/**
 * Port of `TRS4001_Adapter_InquiryOverseaTransferFee` — calls CBS opcode `CIB11001611` (the old
 * `DGBEBankingService.processTRN0044`).
 *
 * NOT a single pass-through call: the old adapter calls CBS TWICE with the same request but two
 * different `feeTypeCode`s (out-bound = 32, then cable = 33) and combines the two results into
 * one response (see `OverseaTransferFeeSbc` for the exact merge logic, replicated faithfully
 * including the old code's hard-coded `inBoundFee = 0.0` and its commented-out third in-bound
 * call). Flagged for extra scrutiny per instructions.
 */
@RestController
@RequestMapping("/TRS4001")
class OverseaTransferFeeCbc(
	private val sbc: OverseaTransferFeeSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<OverseaTransferFeeRequest>): ResponseData<OverseaTransferFeeResponse> =
		sbc.inquire(request)
}
