package kh.bank.dgb.ibs.app.cbs.delete_visited_menu

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteVisitedMenuRequest(
	val userID: String? = null,
	val visitedMenuCode: String? = null,
	val channelTypeCode: String? = null,
)

/** Port of the old `GNB1007_RES_DeleteVisitedMenuVo` — CBS returns an empty object. */
class DeleteVisitedMenuResponse

/** Port of `GNB1007_Adapter_DeleteVisitedMenu` — calls CBS opcode `CIB11300631` (via the old
 *  `DGBEBankingService.processCIB11300631`). `channelTypeCode` is forced to the fixed corporate
 *  banking channel code "01" before forwarding, same as the old adapter. */
@RestController
@RequestMapping("/GNB1007")
class DeleteVisitedMenuCbc(
	private val sbc: DeleteVisitedMenuSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteVisitedMenuRequest>): ResponseData<DeleteVisitedMenuResponse> =
		sbc.delete(request)
}
