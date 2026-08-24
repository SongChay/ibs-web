package kh.bank.dgb.ibs.app.cbs.add_account_group

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AddAccountGroupSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun add(request: RequestData<AddAccountGroupRequest>): ResponseData<AddAccountGroupResponse> =
		connector.post("CIB11002221", request.header?.languageCode, request.body, AddAccountGroupResponse::class.java)
}
