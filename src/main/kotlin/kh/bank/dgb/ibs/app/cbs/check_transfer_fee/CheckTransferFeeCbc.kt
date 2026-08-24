package kh.bank.dgb.ibs.app.cbs.check_transfer_fee

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class CheckTransferFeeRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val accountNo: String? = null,
	val feeTypeCode: String? = null,
	val currencyCode: String? = null,
	val amount: BigDecimal? = null,
	val interBankCode: String? = null,
)

data class CheckTransferFeeResponse(
	val resultYn: String? = null,
	val feeCurrencyCode: String? = null,
	val transferFee: BigDecimal? = null,
)

/** Port of `TRS1005_Adapter_CheckTransferFee` — calls CBS opcode `CIB11000813`
 *  (via the old `DGBEBankingService.processTRN0040`). Straight pass-through. */
@RestController
@RequestMapping("/TRS1005")
class CheckTransferFeeCbc(
	private val sbc: CheckTransferFeeSbc,
) {
	@PostMapping
	fun check(@RequestBody request: RequestData<CheckTransferFeeRequest>): ResponseData<CheckTransferFeeResponse> =
		sbc.check(request)
}
