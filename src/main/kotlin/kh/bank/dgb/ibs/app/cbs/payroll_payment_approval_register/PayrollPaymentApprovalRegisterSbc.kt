package kh.bank.dgb.ibs.app.cbs.payroll_payment_approval_register

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class PayrollPaymentApprovalRegisterSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun register(request: RequestData<PayrollPaymentApprovalRegisterRequest>): ResponseData<PayrollPaymentApprovalRegisterResponse> {
		val result = coreBankingApiConnector.post(
			"CIB11300222",
			request.header?.languageCode,
			request.body,
			PayrollPaymentApprovalRegisterResponse::class.java,
		)
		val body = result.body ?: return result

		val resultYn = if (result.header?.result == true) "Y" else "N"
		return ResponseData(header = result.header, body = body.copy(resultYn = resultYn))
	}
}
