package kh.bank.dgb.ibs.app.cbs.delete_frequently_used_account

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class DeleteFrequentlyUsedAccountSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun delete(request: RequestData<DeleteFrequentlyUsedAccountRequest>): ResponseData<DeleteFrequentlyUsedAccountResponse> {
		val result = coreBankingApiConnector.post(
			"CIB11002731",
			request.header?.languageCode,
			request.body,
			DeleteFrequentlyUsedAccountResponse::class.java,
		)
		val resultYn = if (result.header?.result == true) "Y" else "N"
		return result.copy(body = (result.body ?: DeleteFrequentlyUsedAccountResponse()).copy(resultYn = resultYn))
	}
}
