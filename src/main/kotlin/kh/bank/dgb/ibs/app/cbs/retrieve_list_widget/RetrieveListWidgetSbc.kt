package kh.bank.dgb.ibs.app.cbs.retrieve_list_widget

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `MAN1103_RES_RetrieveWidgetVo` — one row of the CBS `grid01` response grid.
 *  `userID`/`registerDate`/`deleteYN` were `@JsonIgnore`d in the old Vo (never exposed to the
 *  adapter's own client) and are dropped here rather than carried over unused. */
data class RetrieveWidgetItem(
	val widgetCode: String? = null,
)

/** Port of `MAN1103_RES_RetrieveListWrapWidgetVo` — the actual CBS response wire shape. */
data class RetrieveListWidgetCbsResponse(
	val grid01: List<RetrieveWidgetItem>? = null,
)

@Service
class RetrieveListWidgetSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun retrieve(
		request: RequestData<RetrieveListWidgetRequest>,
	): ResponseData<RetrieveListWidgetResponse> {
		val cbsResult = coreBankingApiConnector.post(
			"CIB11300411",
			request.header?.languageCode,
			request.body,
			RetrieveListWidgetCbsResponse::class.java,
		)

		val widgetCodeList = cbsResult.body?.grid01?.map { it.widgetCode }
		return ResponseData(
			header = cbsResult.header,
			body = RetrieveListWidgetResponse(widgetCodeList = widgetCodeList),
		)
	}
}
