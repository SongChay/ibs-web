package kh.bank.dgb.ibs.app.cbs.reject_by_final_approver

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RejectByFinalApproverSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun reject(request: RequestData<RejectByFinalApproverRequest>): ResponseData<RejectByFinalApproverResponse> =
		connector.post("CIB11001022", request.header?.languageCode, request.body, RejectByFinalApproverResponse::class.java)
}
