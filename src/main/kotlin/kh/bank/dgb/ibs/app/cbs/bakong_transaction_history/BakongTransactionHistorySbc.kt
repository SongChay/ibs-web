package kh.bank.dgb.ibs.app.cbs.bakong_transaction_history

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class BakongTransactionHistorySbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<BakongTransactionHistoryRequest>): ResponseData<BakongTransactionHistoryResponse> =
		connector.post("CIB11300913", request.header?.languageCode, request.body, BakongTransactionHistoryResponse::class.java)
}
