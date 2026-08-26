package kh.bank.dgb.ibs.app.cbs.user_access_privilege_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class UserAccessPrivilegeListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<UserAccessPrivilegeListRequest>): ResponseData<UserAccessPrivilegeListResponse> {
		return coreBankingApiConnector.post("CIB11302412", request.header?.languageCode, request.body, UserAccessPrivilegeListResponse::class.java)
	}
}
