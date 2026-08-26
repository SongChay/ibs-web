package kh.bank.dgb.ibs.app.cbs.delete_widget_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteWidgetListRequest(
	val userID: String? = null,
	val widgetCodeList: List<String>? = null,
)

/** Old `MAN1102_RES_DeleteWidgetListVo` has no fields at all — empty response body. */
class DeleteWidgetListResponse

/** Port of `MAN1102_Adapter_DeleteWidgetList` — calls CBS opcode `CIB11300431`
 *  (via the old `DGBEBankingService.processCIB11300431`).
 *
 *  Real logic beyond a plain pass-through: like `MAN1101`, the old adapter re-shapes the flat
 *  `widgetCodeList: List<String>` into the CBS grid wrapper (`userID` + `grid01: List<{widgetCode}>`,
 *  reusing MAN1101's request VOs in the old code) before calling CBS. Replicated in the Sbc. */
@RestController
@RequestMapping("/MAN1102")
class DeleteWidgetListCbc(
	private val deleteWidgetListSbc: DeleteWidgetListSbc,
) {
	@PostMapping
	fun delete(
		@RequestBody request: RequestData<DeleteWidgetListRequest>,
	): ResponseData<DeleteWidgetListResponse> {
		return deleteWidgetListSbc.delete(request)
	}
}
