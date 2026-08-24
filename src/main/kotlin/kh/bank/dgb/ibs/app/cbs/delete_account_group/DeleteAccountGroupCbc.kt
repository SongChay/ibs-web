package kh.bank.dgb.ibs.app.cbs.delete_account_group

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteAccountGroupRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	// Old Vo: getter serialized to CBS as "grid01" (@JsonGetter), setter bound from the client as
	// "frequentAccountGroupNoList" (@JsonSetter) — collapsed here into the CBS wire name since this
	// same object now round-trips to CBS directly.
	@JsonProperty("grid01")
	val frequentAccountGroupNoList: List<FrequentAccountGroupNoItem>? = null,
)

data class FrequentAccountGroupNoItem(
	val frequentAccountGroupNo: Long? = null,
)

data class DeleteAccountGroupResponse(
	val resultYN: String? = null,
)

/** Port of `INF1104_Adapter_DeleteAccountGroup` — calls CBS opcode `CIB11302232` (via the old
 *  `DGBEBankingService.processMGR0007`). Non-pass-through: the old adapter derives
 *  `body.resultYN` ("Y"/"N") from `header.result` after the CBS call; replicated in
 *  `DeleteAccountGroupSbc`. */
@RestController
@RequestMapping("/INF1104")
class DeleteAccountGroupCbc(
	private val sbc: DeleteAccountGroupSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteAccountGroupRequest>): ResponseData<DeleteAccountGroupResponse> =
		sbc.delete(request)
}
