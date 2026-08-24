package kh.bank.dgb.ibs.app.cbs.recipient_account_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RecipientAccountDetailSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RecipientAccountDetailRequest>): ResponseData<RecipientAccountDetailResponse> =
		connector.post("CIB11000812", request.header?.languageCode, request.body, RecipientAccountDetailResponse::class.java)
}
