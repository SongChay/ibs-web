package kh.bank.dgb.ibs.app.cbs.request_status_statistic

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RequestStatusStatisticSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(
		request: RequestData<RequestStatusStatisticRequest>,
	): ResponseData<RequestStatusStatisticResponse> =
		coreBankingApiConnector.post("CIB11302012", request.header?.languageCode, request.body, RequestStatusStatisticResponse::class.java)
}
