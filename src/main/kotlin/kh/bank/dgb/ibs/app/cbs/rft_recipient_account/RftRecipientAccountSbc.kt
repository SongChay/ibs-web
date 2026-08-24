package kh.bank.dgb.ibs.app.cbs.rft_recipient_account

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RftRecipientAccountSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RftRecipientAccountRequest>): ResponseData<RftRecipientAccountResponse> =
		connector.post("CIB11300813", request.header?.languageCode, request.body, RftRecipientAccountResponse::class.java)
}
