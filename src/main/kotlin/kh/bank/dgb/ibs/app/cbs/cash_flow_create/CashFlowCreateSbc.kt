package kh.bank.dgb.ibs.app.cbs.cash_flow_create

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowCreateSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun create(request: RequestData<CashFlowCreateRequest>): ResponseData<CashFlowCreateResponse> =
		connector.post("CIB11003721", request.header?.languageCode, request.body, CashFlowCreateResponse::class.java)
}
