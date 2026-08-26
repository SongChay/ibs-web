package kh.bank.dgb.ibs.app.cbs.add_account_group

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AddAccountGroupSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun add(request: RequestData<AddAccountGroupRequest>): ResponseData<AddAccountGroupResponse> {
		return coreBankingApiConnector.post("CIB11002221", request.header?.languageCode, request.body, AddAccountGroupResponse::class.java)
	}
}
