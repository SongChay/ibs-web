package kh.bank.dgb.ibs.app.cbs.cash_flow_daily_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CashFlowDailyListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CashFlowDailyListRequest>): ResponseData<CashFlowDailyListResponse> {
		return coreBankingApiConnector.post("CIB11003611", request.header?.languageCode, request.body, CashFlowDailyListResponse::class.java)
	}
}
