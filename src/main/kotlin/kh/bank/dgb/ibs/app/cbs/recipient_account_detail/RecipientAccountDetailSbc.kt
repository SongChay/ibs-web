package kh.bank.dgb.ibs.app.cbs.recipient_account_detail

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RecipientAccountDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RecipientAccountDetailRequest>): ResponseData<RecipientAccountDetailResponse> {
		return coreBankingApiConnector.post("CIB11000812", request.header?.languageCode, request.body, RecipientAccountDetailResponse::class.java)
	}
}
