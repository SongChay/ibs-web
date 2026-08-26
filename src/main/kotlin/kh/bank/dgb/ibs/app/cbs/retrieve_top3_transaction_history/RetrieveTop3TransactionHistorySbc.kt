package kh.bank.dgb.ibs.app.cbs.retrieve_top3_transaction_history

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RetrieveTop3TransactionHistorySbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun retrieve(
		request: RequestData<RetrieveTop3TransactionHistoryRequest>,
	): ResponseData<RetrieveTop3TransactionHistoryResponse> =
		coreBankingApiConnector.post(
			"CIB11300415",
			request.header?.languageCode,
			request.body,
			RetrieveTop3TransactionHistoryResponse::class.java,
		)
}
