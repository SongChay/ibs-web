package kh.bank.dgb.ibs.app.cbs.check_duplicated_user_id

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CheckDuplicatedUserIdSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun check(request: RequestData<CheckDuplicatedUserIdRequest>): ResponseData<CheckDuplicatedUserIdResponse> {
		val result = coreBankingApiConnector.post("CIB11002411", request.header?.languageCode, request.body, CheckDuplicatedUserIdResponse::class.java)
		val resultYn = if (result.header?.result == true) "Y" else "N"
		return result.copy(body = (result.body ?: CheckDuplicatedUserIdResponse()).copy(resultYn = resultYn))
	}
}
