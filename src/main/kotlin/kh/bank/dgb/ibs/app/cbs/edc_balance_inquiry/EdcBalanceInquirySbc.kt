package kh.bank.dgb.ibs.app.cbs.edc_balance_inquiry

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcBalanceInquirySbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<EdcBalanceInquiryRequest>): ResponseData<EdcBalanceInquiryResponse> {
		return coreBankingApiConnector.post("CIB11300815", request.header?.languageCode, request.body, EdcBalanceInquiryResponse::class.java)
	}
}
