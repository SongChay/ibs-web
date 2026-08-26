package kh.bank.dgb.ibs.app.cbs.frequently_used_account_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class FrequentlyUsedAccountListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<FrequentlyUsedAccountListRequest>): ResponseData<FrequentlyUsedAccountListResponse> {
		return coreBankingApiConnector.post("CIB11302711", request.header?.languageCode, request.body, FrequentlyUsedAccountListResponse::class.java)
	}
}
