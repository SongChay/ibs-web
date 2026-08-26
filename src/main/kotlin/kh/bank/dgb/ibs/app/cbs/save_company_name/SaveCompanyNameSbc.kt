package kh.bank.dgb.ibs.app.cbs.save_company_name

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class SaveCompanyNameSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun save(request: RequestData<SaveCompanyNameRequest>): ResponseData<SaveCompanyNameResponse> {
		return coreBankingApiConnector.post("CIB11002931", request.header?.languageCode, request.body, SaveCompanyNameResponse::class.java)
	}
}
