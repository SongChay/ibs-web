package kh.bank.dgb.ibs.app.cbs.register_widget_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RegisterWidgetListRequest(
	val userID: String? = null,
	val widgetCodeList: List<String>? = null,
)

/** Old `MAN1101_RES_RegisterWidgetListVo` has no fields at all — empty response body. */
class RegisterWidgetListResponse

/** Port of `MAN1101_Adapter_RegisterWidgetList` — calls CBS opcode `CIB11300421`
 *  (via the old `DGBEBankingService.processCIB11300421`).
 *
 *  Real logic beyond a plain pass-through: the old adapter re-shapes the flat
 *  `widgetCodeList: List<String>` into the CBS grid wrapper (`userID` + `grid01: List<{widgetCode}>`)
 *  before calling CBS. Replicated in the Sbc. */
@RestController
@RequestMapping("/MAN1101")
class RegisterWidgetListCbc(
	private val sbc: RegisterWidgetListSbc,
) {
	@PostMapping
	fun register(
		@RequestBody request: RequestData<RegisterWidgetListRequest>,
	): ResponseData<RegisterWidgetListResponse> = sbc.register(request)
}
