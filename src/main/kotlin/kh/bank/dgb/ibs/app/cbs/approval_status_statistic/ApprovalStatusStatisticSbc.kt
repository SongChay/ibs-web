package kh.bank.dgb.ibs.app.cbs.approval_status_statistic

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class ApprovalStatusStatisticSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(
		request: RequestData<ApprovalStatusStatisticRequest>,
	): ResponseData<ApprovalStatusStatisticResponse> =
		connector.post("CIB11300413", request.header?.languageCode, request.body, ApprovalStatusStatisticResponse::class.java)
}
