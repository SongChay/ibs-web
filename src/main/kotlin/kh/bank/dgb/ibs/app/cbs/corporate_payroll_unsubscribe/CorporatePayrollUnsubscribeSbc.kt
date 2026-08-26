package kh.bank.dgb.ibs.app.cbs.corporate_payroll_unsubscribe

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CorporatePayrollUnsubscribeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun unsubscribe(request: RequestData<CorporatePayrollUnsubscribeRequest>): ResponseData<CorporatePayrollUnsubscribeResponse> {
		val result = coreBankingApiConnector.post("CIB11300123", request.header?.languageCode, request.body, CorporatePayrollUnsubscribeResponse::class.java)
		val body = result.body ?: return result

		val resultYn = if (result.header?.result == true) "Y" else "N"
		return ResponseData(header = result.header, body = body.copy(resultYn = resultYn))
	}
}
