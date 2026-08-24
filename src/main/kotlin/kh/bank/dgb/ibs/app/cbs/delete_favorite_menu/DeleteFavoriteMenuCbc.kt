package kh.bank.dgb.ibs.app.cbs.delete_favorite_menu

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class FavoriteMenuSeqNo(
	val seqNo: Int = 0,
	val level1MenuCode: String? = null,
	val level2MenuCode: String? = null,
)

/** Port of `GNB1001_REQ_RegisterListFavoriteMenuVo` — its old Java Vo used an asymmetric
 *  `@JsonGetter("grid01")`/`@JsonSetter("favoriteList")` pair: read from the client as
 *  `favoriteList` but sent to CBS as `grid01`. Kept exactly. `channelTypeCode` here is a body
 *  field CBS itself expects (distinct from the transport-level channel code the connector already
 *  fills in) — the old adapter always overwrote whatever the client sent with the fixed corporate
 *  banking channel code "01" before forwarding; replicated in the Sbc. */
data class DeleteFavoriteMenuRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	@param:JsonProperty("favoriteList") @get:JsonProperty("grid01")
	val favoriteList: List<FavoriteMenuSeqNo>? = null,
)

/** Port of the old `GNB1001_RES_RegisterListFavoriteMenuVo` — CBS returns an empty object. */
class DeleteFavoriteMenuResponse

/** Port of `GNB1001_Adapter_DeleteFavoriteMenu` — calls CBS opcode `CIB11300521` (via the old
 *  `DGBEBankingService.processCIB11300521`). */
@RestController
@RequestMapping("/GNB1001")
class DeleteFavoriteMenuCbc(
	private val sbc: DeleteFavoriteMenuSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteFavoriteMenuRequest>): ResponseData<DeleteFavoriteMenuResponse> =
		sbc.delete(request)
}
