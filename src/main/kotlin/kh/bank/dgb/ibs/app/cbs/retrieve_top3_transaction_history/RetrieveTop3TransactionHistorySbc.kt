package kh.bank.dgb.ibs.app.cbs.retrieve_top3_transaction_history

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RetrieveTop3TransactionHistorySbc(
	private val connector: CoreBankingApiConnector,
) {
	fun retrieve(
		request: RequestData<RetrieveTop3TransactionHistoryRequest>,
	): ResponseData<RetrieveTop3TransactionHistoryResponse> =
		connector.post(
			"CIB11300415",
			request.header?.languageCode,
			request.body,
			RetrieveTop3TransactionHistoryResponse::class.java,
		)
}
