package kh.bank.dgb.ibs.app.cbs.bakong_recipient_account_detail

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class BakongRecipientAccountDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<BakongRecipientAccountDetailRequest>): ResponseData<BakongRecipientAccountDetailResponse> {
		return coreBankingApiConnector.post("CIB11300814", request.header?.languageCode, request.body, BakongRecipientAccountDetailResponse::class.java)
	}
}
