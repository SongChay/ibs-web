package kh.bank.dgb.ibs.app.cbs.cash_flow_update

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowUpdateSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<CashFlowUpdateRequest>): ResponseData<CashFlowUpdateResponse> =
		connector.post("CIB11003631", request.header?.languageCode, request.body, CashFlowUpdateResponse::class.java)
}
