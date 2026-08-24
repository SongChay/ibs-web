package kh.bank.dgb.ibs.app.cbs.edc_balance_validation

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcBalanceValidationSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun validate(request: RequestData<EdcBalanceValidationRequest>): ResponseData<EdcBalanceValidationResponse> =
		connector.post("CIB11300816", request.header?.languageCode, request.body, EdcBalanceValidationResponse::class.java)
}
