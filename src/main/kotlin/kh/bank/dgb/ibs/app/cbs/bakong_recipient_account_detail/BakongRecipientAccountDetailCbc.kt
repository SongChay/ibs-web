package kh.bank.dgb.ibs.app.cbs.bakong_recipient_account_detail

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class BakongRecipientAccountDetailRequest(
	val receiverBankCode: String? = null,
	val receiverAccountNo: String? = null,
)

data class BakongRecipientAccountDetailResponse(
	val bankCode: String? = null,
	val accountName: String? = null,
	val accountNo: String? = null,
	val currencyCode: String? = null,
)

/** Port of `TRS0814_Adapter_inquiryBakongRecipientAccountDetail` — calls CBS opcode `CIB11300814`
 *  (via the old `DGBEBankingService.processCIB11300814`). Straight pass-through, no adapter-side
 *  logic beyond setting the channel type code, which the connector already handles. */
@RestController
@RequestMapping("/TRS0814")
class BakongRecipientAccountDetailCbc(
	private val bakongRecipientAccountDetailSbc: BakongRecipientAccountDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<BakongRecipientAccountDetailRequest>): ResponseData<BakongRecipientAccountDetailResponse> {
		return bakongRecipientAccountDetailSbc.inquire(request)
	}
}
