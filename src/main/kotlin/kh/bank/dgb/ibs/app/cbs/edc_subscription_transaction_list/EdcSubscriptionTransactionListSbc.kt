package kh.bank.dgb.ibs.app.cbs.edc_subscription_transaction_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcSubscriptionTransactionListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<EdcSubscriptionTransactionListRequest>): ResponseData<EdcSubscriptionTransactionListResponse> =
		connector.post("CIB11102513", request.header?.languageCode, request.body, EdcSubscriptionTransactionListResponse::class.java)
}
