package kh.bank.dgb.ibs.app.cbs.inquiry_favorite_menu_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of the old `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING` constant. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

@Service
class InquiryFavoriteMenuListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<InquiryFavoriteMenuListRequest>): ResponseData<InquiryFavoriteMenuListResponse> {
		val body = request.body?.copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		return coreBankingApiConnector.post("CIB11300511", request.header?.languageCode, body, InquiryFavoriteMenuListResponse::class.java)
	}
}
