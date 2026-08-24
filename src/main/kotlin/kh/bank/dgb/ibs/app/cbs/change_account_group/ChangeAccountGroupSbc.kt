package kh.bank.dgb.ibs.app.cbs.change_account_group

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class ChangeAccountGroupSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun change(request: RequestData<ChangeAccountGroupRequest>): ResponseData<ChangeAccountGroupResponse> =
		connector.post("CIB11002231", request.header?.languageCode, request.body, ChangeAccountGroupResponse::class.java)
}
