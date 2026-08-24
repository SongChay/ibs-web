package kh.bank.dgb.ibs.app.cbs.delete_approval_memo

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class DeleteApprovalMemoSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<DeleteApprovalMemoRequest>): ResponseData<DeleteApprovalMemoResponse> =
		connector.post("CIB11003231", request.header?.languageCode, request.body, DeleteApprovalMemoResponse::class.java)
}
