package kh.bank.dgb.ibs.app.cbs.edc_subscription_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcSubscriptionListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<EdcSubscriptionListRequest>): ResponseData<EdcSubscriptionListResponse> =
		connector.post("CIB11102512", request.header?.languageCode, request.body, EdcSubscriptionListResponse::class.java)
}
