package kh.bank.dgb.ibs.app.cbs.add_visited_menu

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddVisitedMenuRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val visitedMenuCode: String? = null,
)

/** Port of the old `GNB1006_RES_RegisterVisitedMenuVo` — CBS returns an empty object. */
class AddVisitedMenuResponse

/** Port of `GNB1006_Adapter_AddVisitedMenu` — calls CBS opcode `CIB11300621` (via the old
 *  `DGBEBankingService.processCIB11300621`). `channelTypeCode` is forced to the fixed corporate
 *  banking channel code "01" before forwarding, same as the old adapter. */
@RestController
@RequestMapping("/GNB1006")
class AddVisitedMenuCbc(
	private val sbc: AddVisitedMenuSbc,
) {
	@PostMapping
	fun add(@RequestBody request: RequestData<AddVisitedMenuRequest>): ResponseData<AddVisitedMenuResponse> =
		sbc.add(request)
}
