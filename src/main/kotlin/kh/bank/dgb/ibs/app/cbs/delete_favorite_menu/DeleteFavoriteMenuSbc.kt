package kh.bank.dgb.ibs.app.cbs.delete_favorite_menu

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of the old `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING` constant. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

@Service
class DeleteFavoriteMenuSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<DeleteFavoriteMenuRequest>): ResponseData<DeleteFavoriteMenuResponse> {
		val body = request.body?.copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		return coreBankingApiConnector.post("CIB11300521", request.header?.languageCode, body, DeleteFavoriteMenuResponse::class.java)
	}
}
