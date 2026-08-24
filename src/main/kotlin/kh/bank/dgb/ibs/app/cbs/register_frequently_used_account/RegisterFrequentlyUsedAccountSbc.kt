package kh.bank.dgb.ibs.app.cbs.register_frequently_used_account

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RegisterFrequentlyUsedAccountSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun register(request: RequestData<RegisterFrequentlyUsedAccountRequest>): ResponseData<RegisterFrequentlyUsedAccountResponse> {
		val result = connector.post(
			"CIB11002721",
			request.header?.languageCode,
			request.body,
			RegisterFrequentlyUsedAccountResponse::class.java,
		)
		val resultYn = if (result.header?.result == true) "Y" else "N"
		return result.copy(body = (result.body ?: RegisterFrequentlyUsedAccountResponse()).copy(resultYn = resultYn))
	}
}
