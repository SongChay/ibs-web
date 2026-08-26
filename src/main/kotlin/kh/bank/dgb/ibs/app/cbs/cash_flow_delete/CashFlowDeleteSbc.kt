package kh.bank.dgb.ibs.app.cbs.cash_flow_delete

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowDeleteSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<CashFlowDeleteRequest>): ResponseData<CashFlowDeleteResponse> {
		return coreBankingApiConnector.post("CIB11003632", request.header?.languageCode, request.body, CashFlowDeleteResponse::class.java)
	}
}
