package kh.bank.dgb.ibs.app.cbs.delete_widget_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `MAN1101_REQ_WidgetListVo` — one row of the `grid01` CBS request grid. */
data class DeleteWidgetListItem(
	val widgetCode: String? = null,
)

/** Port of `MAN1101_REQ_RegisterWrapWidgetListVo`, reused by the old delete adapter — the
 *  actual CBS request wire shape. */
data class DeleteWidgetListCbsRequest(
	val userID: String? = null,
	val grid01: List<DeleteWidgetListItem>? = null,
)

@Service
class DeleteWidgetListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun delete(
		request: RequestData<DeleteWidgetListRequest>,
	): ResponseData<DeleteWidgetListResponse> {
		val body = request.body
		val cbsRequest = DeleteWidgetListCbsRequest(
			userID = body?.userID,
			grid01 = body?.widgetCodeList?.map { DeleteWidgetListItem(widgetCode = it) },
		)

		return coreBankingApiConnector.post(
			"CIB11300431",
			request.header?.languageCode,
			cbsRequest,
			DeleteWidgetListResponse::class.java,
		)
	}
}
