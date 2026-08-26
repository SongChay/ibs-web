package kh.bank.dgb.ibs.app.cbs.inquiry_visited_menu_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of the old `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING` constant. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

@Service
class InquiryVisitedMenuListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<InquiryVisitedMenuListRequest>): ResponseData<InquiryVisitedMenuListResponse> {
		val body = request.body?.copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		return coreBankingApiConnector.post("CIB11300611", request.header?.languageCode, body, InquiryVisitedMenuListResponse::class.java)
	}
}
