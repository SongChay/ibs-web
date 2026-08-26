package kh.bank.dgb.ibs.app.cbs.delete_sub_user

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class DeleteSubUserSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<DeleteSubUserRequest>): ResponseData<DeleteSubUserResponse> {
		return coreBankingApiConnector.post("CIB11002331", request.header?.languageCode, request.body, DeleteSubUserResponse::class.java)
	}
}
