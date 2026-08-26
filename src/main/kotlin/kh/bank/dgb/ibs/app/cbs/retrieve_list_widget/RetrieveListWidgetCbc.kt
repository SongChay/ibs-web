package kh.bank.dgb.ibs.app.cbs.retrieve_list_widget

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RetrieveListWidgetRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

data class RetrieveListWidgetResponse(
	val widgetCodeList: List<String?>? = null,
)

/** Port of `MAN1103_Adapter_RetrieveListWidget` — calls CBS opcode `CIB11300411`
 *  (via the old `DGBEBankingService.processCIB11300411`).
 *
 *  Real logic beyond a plain pass-through: CBS returns a grid of widget rows
 *  (`grid01: [{userID, widgetCode, registerDate, deleteYN}, ...]`); the old adapter flattens
 *  that down to just the `widgetCode` strings. Replicated in the Sbc. */
@RestController
@RequestMapping("/MAN1103")
class RetrieveListWidgetCbc(
	private val retrieveListWidgetSbc: RetrieveListWidgetSbc,
) {
	@PostMapping
	fun retrieve(
		@RequestBody request: RequestData<RetrieveListWidgetRequest>,
	): ResponseData<RetrieveListWidgetResponse> {
		return retrieveListWidgetSbc.retrieve(request)
	}
}
