package kh.bank.dgb.ibs.app.cbs.cash_flow_yearly_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowYearlyListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CashFlowYearlyListRequest>): ResponseData<CashFlowYearlyListResponse> {
		return coreBankingApiConnector.post("CIB11003511", request.header?.languageCode, request.body, CashFlowYearlyListResponse::class.java)
	}
}
