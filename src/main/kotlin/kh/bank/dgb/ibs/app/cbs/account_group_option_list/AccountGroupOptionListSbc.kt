package kh.bank.dgb.ibs.app.cbs.account_group_option_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AccountGroupOptionListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<AccountGroupOptionListRequest>): ResponseData<AccountGroupOptionListResponse> =
		connector.post("CIB11002712", request.header?.languageCode, request.body, AccountGroupOptionListResponse::class.java)
}
