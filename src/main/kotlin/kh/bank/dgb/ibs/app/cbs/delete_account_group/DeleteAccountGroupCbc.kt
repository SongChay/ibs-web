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
	// "frequentAccountGroupNoList" (@JsonSetter). A bare @get:JsonProperty alone does NOT preserve
	// that asymmetry (empirically verified: it renames both directions, so the client's own
	// "frequentAccountGroupNoList" would silently deserialize to null) — needs the explicit
	// @param:JsonProperty alongside it to pin deserialization to the client-facing name.
	@param:JsonProperty("frequentAccountGroupNoList") @get:JsonProperty("grid01")
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
	private val deleteAccountGroupSbc: DeleteAccountGroupSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteAccountGroupRequest>): ResponseData<DeleteAccountGroupResponse> {
		return deleteAccountGroupSbc.delete(request)
	}
}
