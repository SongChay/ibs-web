package kh.bank.dgb.ibs.app.cbs.wing_transfer_fee

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class WingTransferFeeRequest(
	val wingTransferTypeCode: String? = null,
	val currencyCode: String? = null,
	val amount: BigDecimal? = null,
	val channelTypeCode: String? = null,
)

data class WingTransferFeeResponse(
	val resultYn: String? = null,
	val feeCurrencyCode: String? = null,
	val transferFee: BigDecimal? = null,
	val wingFee: BigDecimal? = null,
)

/** Port of `TRS5003_Adapter_GetWingTransferFee` — calls CBS opcode `CIB11001812` (the old
 *  `DGBEBankingService.processWNG009`). */
@RestController
@RequestMapping("/TRS5003")
class WingTransferFeeCbc(
	private val sbc: WingTransferFeeSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<WingTransferFeeRequest>): ResponseData<WingTransferFeeResponse> =
		sbc.inquire(request)
}
