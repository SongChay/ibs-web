package kh.bank.dgb.ibs.app.cbs.domestic_bank_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class DomesticBankListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<DomesticBankListRequest>): ResponseData<DomesticBankListResponse> {
		return coreBankingApiConnector.post("CIB11000013", request.header?.languageCode, request.body, DomesticBankListResponse::class.java)
	}
}
