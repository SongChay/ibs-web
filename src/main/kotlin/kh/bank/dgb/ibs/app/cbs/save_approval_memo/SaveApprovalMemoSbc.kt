package kh.bank.dgb.ibs.app.cbs.save_approval_memo

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class SaveApprovalMemoSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun save(request: RequestData<SaveApprovalMemoRequest>): ResponseData<SaveApprovalMemoResponse> {
		return coreBankingApiConnector.post("CIB11003221", request.header?.languageCode, request.body, SaveApprovalMemoResponse::class.java)
	}
}
