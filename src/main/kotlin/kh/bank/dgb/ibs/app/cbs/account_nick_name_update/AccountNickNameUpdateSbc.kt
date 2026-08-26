package kh.bank.dgb.ibs.app.cbs.account_nick_name_update

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AccountNickNameUpdateSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<AccountNickNameUpdateRequest>): ResponseData<AccountNickNameUpdateResponse> {
		return coreBankingApiConnector.post("CIB11000631", request.header?.languageCode, request.body, AccountNickNameUpdateResponse::class.java)
	}
}
