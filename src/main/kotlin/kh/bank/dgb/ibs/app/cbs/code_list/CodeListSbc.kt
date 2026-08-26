package kh.bank.dgb.ibs.app.cbs.code_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CodeListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CodeListRequest>): ResponseData<CodeListResponse> {
		return coreBankingApiConnector.post("CIB11000012", request.header?.languageCode, request.body, CodeListResponse::class.java)
	}
}
