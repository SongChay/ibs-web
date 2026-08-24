package kh.bank.dgb.ibs.app.cbs.cancel_approval_request

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CancelApprovalRequestSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun cancel(request: RequestData<CancelApprovalRequestRequest>): ResponseData<CancelApprovalRequestResponse> =
		connector.post("CIB11003031", request.header?.languageCode, request.body, CancelApprovalRequestResponse::class.java)
}
