package kh.bank.dgb.ibs.app.cbs.account_info_e_statement

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AccountInfoEStatementSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun retrieve(request: RequestData<AccountInfoEStatementRequest>): ResponseData<AccountInfoEStatementResponse> =
		connector.post("CIB11300914", request.header?.languageCode, request.body, AccountInfoEStatementResponse::class.java)
}
