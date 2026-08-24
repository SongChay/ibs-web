package kh.bank.dgb.ibs.app.cbs.update_frequently_used_account

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class UpdateFrequentlyUsedAccountSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<UpdateFrequentlyUsedAccountRequest>): ResponseData<UpdateFrequentlyUsedAccountResponse> {
		val result = connector.post(
			"CIB11002831",
			request.header?.languageCode,
			request.body,
			UpdateFrequentlyUsedAccountResponse::class.java,
		)
		return if (result.header?.result == true) {
			result.copy(body = (result.body ?: UpdateFrequentlyUsedAccountResponse()).copy(resultYn = "Y"))
		} else {
			result
		}
	}
}
