package kh.bank.dgb.ibs.app.cbs.add_visited_menu

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of the old `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING` constant. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

@Service
class AddVisitedMenuSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun add(request: RequestData<AddVisitedMenuRequest>): ResponseData<AddVisitedMenuResponse> {
		val body = request.body?.copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		return connector.post("CIB11300621", request.header?.languageCode, body, AddVisitedMenuResponse::class.java)
	}
}
