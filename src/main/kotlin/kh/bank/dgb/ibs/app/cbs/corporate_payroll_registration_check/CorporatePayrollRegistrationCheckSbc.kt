package kh.bank.dgb.ibs.app.cbs.corporate_payroll_registration_check

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CorporatePayrollRegistrationCheckSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun check(request: RequestData<CorporatePayrollRegistrationCheckRequest>): ResponseData<CorporatePayrollRegistrationCheckResponse> {
		val result = connector.post("CIB11300122", request.header?.languageCode, request.body, CorporatePayrollRegistrationCheckResponse::class.java)
		val body = result.body ?: return result

		return ResponseData(header = result.header, body = body.copy(register = !body.grid01.isNullOrEmpty()))
	}
}
