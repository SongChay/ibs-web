package kh.bank.dgb.ibs.app.cbs.wing_account_info

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class WingAccountInfoSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<WingAccountInfoRequest>): ResponseData<WingAccountInfoResponse> {
		return coreBankingApiConnector.post("CIB11001811", request.header?.languageCode, request.body, WingAccountInfoResponse::class.java)
	}
}
