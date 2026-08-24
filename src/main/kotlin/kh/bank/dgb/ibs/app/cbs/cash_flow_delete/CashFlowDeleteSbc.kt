package kh.bank.dgb.ibs.app.cbs.cash_flow_delete

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowDeleteSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<CashFlowDeleteRequest>): ResponseData<CashFlowDeleteResponse> =
		connector.post("CIB11003632", request.header?.languageCode, request.body, CashFlowDeleteResponse::class.java)
}
