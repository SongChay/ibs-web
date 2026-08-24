package kh.bank.dgb.ibs.app.cbs.recipient_account_detail

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RecipientAccountDetailRequest(
	val accountNo: String? = null,
)

data class RecipientAccountDetailResponse(
	val customerNo: String? = null,
	val accountNo: String? = null,
	val accountName: String? = null,
	val accountNameForCorporate: String? = null,
	val openDate: String? = null,
	val closeDate: String? = null,
	val bankCode: String? = null,
	val branchCode: String? = null,
	val branchName: String? = null,
	val currencyCode: String? = null,
)

/** Port of `TRS1004_Adapter_InquiryRecipientAccountDetail` — calls CBS opcode `CIB11000812`
 *  (via the old `DGBEBankingService.processACC0001`). Straight pass-through. */
@RestController
@RequestMapping("/TRS1004")
class RecipientAccountDetailCbc(
	private val sbc: RecipientAccountDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RecipientAccountDetailRequest>): ResponseData<RecipientAccountDetailResponse> =
		sbc.inquire(request)
}
