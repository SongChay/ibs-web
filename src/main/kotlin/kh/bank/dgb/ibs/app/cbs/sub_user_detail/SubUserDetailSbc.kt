package kh.bank.dgb.ibs.app.cbs.sub_user_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class SubUserDetailSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<SubUserDetailRequest>): ResponseData<SubUserDetailResponse> =
		connector.post("CIB11302511", request.header?.languageCode, request.body, SubUserDetailResponse::class.java)
}
