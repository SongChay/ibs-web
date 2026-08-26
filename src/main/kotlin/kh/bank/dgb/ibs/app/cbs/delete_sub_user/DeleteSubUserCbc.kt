package kh.bank.dgb.ibs.app.cbs.delete_sub_user

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteSubUserRequest(
	val loginUserID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	// Old Vo: getter serialized to CBS as "grid01", setter bound from the client as "subUserIDList".
	// A bare @get:JsonProperty alone does NOT preserve that asymmetry (empirically verified: it
	// renames both directions, so the client's own "subUserIDList" would silently deserialize to
	// null) — needs the explicit @param:JsonProperty alongside it to pin deserialization to the
	// client-facing name.
	@param:JsonProperty("subUserIDList") @get:JsonProperty("grid01")
	val subUserIDList: List<UserIdItem>? = null,
)

data class UserIdItem(
	val userID: String? = null,
)

data class DeleteSubUserResponse(
	// Old Vo: getter serialized to the client as "subUserIDList", setter bound from CBS as
	// "grid01" — kept exactly: accepted from CBS via "grid01" (JsonAlias, deserialize-only),
	// serialized back to the client under the default "subUserIDList" name.
	@JsonAlias("grid01")
	val subUserIDList: List<UserIdItem>? = null,
)

/** Port of `INF2002_Adapter_DeleteSubUser` — calls CBS opcode `CIB11002331` (via the old
 *  `DGBEBankingService.processMGR0014`). */
@RestController
@RequestMapping("/INF2002")
class DeleteSubUserCbc(
	private val deleteSubUserSbc: DeleteSubUserSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteSubUserRequest>): ResponseData<DeleteSubUserResponse> {
		return deleteSubUserSbc.delete(request)
	}
}
