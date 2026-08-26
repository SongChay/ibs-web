package kh.bank.dgb.ibs.app.cbs.edc_subscription_update

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcSubscriptionUpdateSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<EdcSubscriptionUpdateRequest>): ResponseData<EdcSubscriptionUpdateResponse> {
		return coreBankingApiConnector.post("CIB11102531", request.header?.languageCode, request.body, EdcSubscriptionUpdateResponse::class.java)
	}
}
