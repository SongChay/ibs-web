package kh.bank.dgb.ibs.app.cbs.update_sub_user_service_status

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class UpdateSubUserServiceStatusSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<UpdateSubUserServiceStatusRequest>): ResponseData<UpdateSubUserServiceStatusResponse> {
		return coreBankingApiConnector.post("CIB11002332", request.header?.languageCode, request.body, UpdateSubUserServiceStatusResponse::class.java)
	}
}
