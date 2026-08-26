package kh.bank.dgb.ibs.app.cbs.approver_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class ApproverListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<ApproverListRequest>): ResponseData<ApproverListResponse> {
		return coreBankingApiConnector.post("CIB11001002", request.header?.languageCode, request.body, ApproverListResponse::class.java)
	}
}
