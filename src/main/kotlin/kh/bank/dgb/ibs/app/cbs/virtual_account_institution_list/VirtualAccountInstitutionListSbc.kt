package kh.bank.dgb.ibs.app.cbs.virtual_account_institution_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class VirtualAccountInstitutionListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<VirtualAccountInstitutionListRequest>): ResponseData<VirtualAccountInstitutionListResponse> {
		return coreBankingApiConnector.post(
			"CIB11002013",
			request.header?.languageCode,
			request.body,
			VirtualAccountInstitutionListResponse::class.java,
		)
	}
}
