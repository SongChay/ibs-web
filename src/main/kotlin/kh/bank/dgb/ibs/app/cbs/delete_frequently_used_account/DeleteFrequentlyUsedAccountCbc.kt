package kh.bank.dgb.ibs.app.cbs.delete_frequently_used_account

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteFrequentlyUsedAccountRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	// Old Vo: getter serialized to CBS as "grid01", setter bound from the client as
	// "seqNoList" — kept as the CBS wire name here.
	@JsonProperty("grid01")
	val seqNoList: List<SeqNoItem>? = null,
)

data class SeqNoItem(
	val seqNo: Int? = null,
)

data class DeleteFrequentlyUsedAccountResponse(
	val resultYn: String? = null,
)

/** Port of `INF4002_Adapter_DeleteFrequentlyUsedAccount` — calls CBS opcode `CIB11002731` (via
 *  the old `DGBEBankingService.processTRN0015`). Non-pass-through: the old adapter derives
 *  `body.resultYn` ("Y"/"N") from `header.result` after the CBS call; replicated in
 *  `DeleteFrequentlyUsedAccountSbc`. */
@RestController
@RequestMapping("/INF4002")
class DeleteFrequentlyUsedAccountCbc(
	private val sbc: DeleteFrequentlyUsedAccountSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteFrequentlyUsedAccountRequest>): ResponseData<DeleteFrequentlyUsedAccountResponse> =
		sbc.delete(request)
}
