package kh.bank.dgb.ibs.app.cbs.rft_recipient_account

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class RftRecipientAccountRequest(
	val userID: String? = null,
	val accountNo: String? = null,
	val accountName: String? = null,
	val bankCode: String? = null,
	val receiverAccountNo: String? = null,
	val currencyCode: String? = null,
	val transferAmount: BigDecimal? = null,
)

data class RftRecipientAccountResponse(
	val receiverAccountNo: String? = null,
	val receiverName: String? = null,
	val currencyCode: String? = null,
	val transferAmount: BigDecimal? = null,
	val senderFee: BigDecimal? = null,
	val centerFee: BigDecimal? = null,
	val receiverFee: BigDecimal? = null,
)

/** Port of `TRS1007_Adapter_InquiryRFTRecipientAccount` — calls CBS opcode `CIB11300813`
 *  (via the old `DGBEBankingService.processCIB11300813`). Straight pass-through. */
@RestController
@RequestMapping("/TRS1007")
class RftRecipientAccountCbc(
	private val rftRecipientAccountSbc: RftRecipientAccountSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RftRecipientAccountRequest>): ResponseData<RftRecipientAccountResponse> {
		return rftRecipientAccountSbc.inquire(request)
	}
}
