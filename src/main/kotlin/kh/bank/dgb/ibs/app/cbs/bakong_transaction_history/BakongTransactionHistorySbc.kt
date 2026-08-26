package kh.bank.dgb.ibs.app.cbs.bakong_transaction_history

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class BakongTransactionHistorySbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<BakongTransactionHistoryRequest>): ResponseData<BakongTransactionHistoryResponse> {
		return coreBankingApiConnector.post("CIB11300913", request.header?.languageCode, request.body, BakongTransactionHistoryResponse::class.java)
	}
}
