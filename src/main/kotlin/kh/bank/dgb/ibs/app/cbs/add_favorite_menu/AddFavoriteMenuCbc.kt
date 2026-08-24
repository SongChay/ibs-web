package kh.bank.dgb.ibs.app.cbs.add_favorite_menu

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

/** Port of `GNB1002_REQ_RegisterListFavoriteMenuVo` — same asymmetric `favoriteList`(in)/`grid01`
 *  (out to CBS) wire mapping as `GNB1001`. `channelTypeCode` is forced to the fixed corporate
 *  banking channel code "01" before forwarding, same as the old adapter. */
data class AddFavoriteMenuRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	@param:JsonProperty("favoriteList") @get:JsonProperty("grid01")
	val favoriteList: List<FavoriteMenuSeqNo>? = null,
)

/** Port of the old `GNB1002_RES_RegisterListFavoriteMenuVo` — CBS returns an empty object. */
class AddFavoriteMenuResponse

/** Port of `GNB1002_Adapter_AddFavoriteMenu` — calls CBS opcode `CIB11300531` (via the old
 *  `DGBEBankingService.processCIB11300531`). */
@RestController
@RequestMapping("/GNB1002")
class AddFavoriteMenuCbc(
	private val sbc: AddFavoriteMenuSbc,
) {
	@PostMapping
	fun add(@RequestBody request: RequestData<AddFavoriteMenuRequest>): ResponseData<AddFavoriteMenuResponse> =
		sbc.add(request)
}
