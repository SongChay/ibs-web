package kh.bank.dgb.ibs.app.cbs.approve_requests

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class ApproveRequestsSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun approve(request: RequestData<ApproveRequestsRequest>): ResponseData<ApproveRequestsResponse> =
		connector.post("CIB11303331", request.header?.languageCode, request.body, ApproveRequestsResponse::class.java)
}
