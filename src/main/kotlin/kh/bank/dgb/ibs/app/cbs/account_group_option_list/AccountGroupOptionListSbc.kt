package kh.bank.dgb.ibs.app.cbs.account_group_option_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AccountGroupOptionListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<AccountGroupOptionListRequest>): ResponseData<AccountGroupOptionListResponse> {
		return coreBankingApiConnector.post("CIB11002712", request.header?.languageCode, request.body, AccountGroupOptionListResponse::class.java)
	}
}
