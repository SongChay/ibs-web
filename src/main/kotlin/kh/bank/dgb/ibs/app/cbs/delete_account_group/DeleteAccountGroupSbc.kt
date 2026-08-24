package kh.bank.dgb.ibs.app.cbs.delete_account_group

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class DeleteAccountGroupSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<DeleteAccountGroupRequest>): ResponseData<DeleteAccountGroupResponse> {
		val result = connector.post("CIB11302232", request.header?.languageCode, request.body, DeleteAccountGroupResponse::class.java)
		val resultYN = if (result.header?.result == true) "Y" else "N"
		return result.copy(body = (result.body ?: DeleteAccountGroupResponse()).copy(resultYN = resultYN))
	}
}
