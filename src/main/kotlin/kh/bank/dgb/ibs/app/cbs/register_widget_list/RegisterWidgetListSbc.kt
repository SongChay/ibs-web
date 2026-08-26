package kh.bank.dgb.ibs.app.cbs.register_widget_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `MAN1101_REQ_WidgetListVo` — one row of the `grid01` CBS request grid. */
data class WidgetListItem(
	val widgetCode: String? = null,
)

/** Port of `MAN1101_REQ_RegisterWrapWidgetListVo` — the actual CBS request wire shape. */
data class RegisterWidgetListCbsRequest(
	val userID: String? = null,
	val grid01: List<WidgetListItem>? = null,
)

@Service
class RegisterWidgetListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun register(
		request: RequestData<RegisterWidgetListRequest>,
	): ResponseData<RegisterWidgetListResponse> {
		val body = request.body
		val cbsRequest = RegisterWidgetListCbsRequest(
			userID = body?.userID,
			grid01 = body?.widgetCodeList?.map { WidgetListItem(widgetCode = it) },
		)

		return coreBankingApiConnector.post(
			"CIB11300421",
			request.header?.languageCode,
			cbsRequest,
			RegisterWidgetListResponse::class.java,
		)
	}
}
