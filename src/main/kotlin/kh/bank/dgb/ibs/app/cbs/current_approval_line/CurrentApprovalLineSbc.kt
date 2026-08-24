package kh.bank.dgb.ibs.app.cbs.current_approval_line

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CurrentApprovalLineSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CurrentApprovalLineRequest>): ResponseData<CurrentApprovalLineResponse> =
		connector.post("CIB11001001", request.header?.languageCode, request.body, CurrentApprovalLineResponse::class.java)
}
