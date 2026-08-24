package kh.bank.dgb.ibs.app.cbs.update_frequently_used_account

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UpdateFrequentlyUsedAccountRequest(
	val seqNo: Int? = null,
	val userID: String? = null,
	val frequentAccountGroupNo: Long? = null,
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	val interBankCode: String? = null,
	val accountNo: String? = null,
	val receiverName: String? = null,
	val nickname: String? = null,
	val currencyCode: String? = null,
)

data class UpdateFrequentlyUsedAccountResponse(
	val resultYn: String? = null,
)

/** Port of `INF4004_Adapter_UpdateFrequentlyUsedAccount` — calls CBS opcode `CIB11002831` (via
 *  the old `DGBEBankingService.processTRN0014`). Non-pass-through: the old adapter sets
 *  `body.resultYn = "Y"` ONLY when `header.result` is true (no "N" branch — on failure the field
 *  is left exactly as CBS returned it); replicated in `UpdateFrequentlyUsedAccountSbc`. */
@RestController
@RequestMapping("/INF4004")
class UpdateFrequentlyUsedAccountCbc(
	private val sbc: UpdateFrequentlyUsedAccountSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<UpdateFrequentlyUsedAccountRequest>): ResponseData<UpdateFrequentlyUsedAccountResponse> =
		sbc.update(request)
}
