package kh.bank.dgb.ibs.app.cbs.register_approval_line

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RegisterApprovalLineSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun register(request: RequestData<RegisterApprovalLineRequest>): ResponseData<RegisterApprovalLineResponse> =
		connector.post("CIB11302621", request.header?.languageCode, request.body, RegisterApprovalLineResponse::class.java)
}
