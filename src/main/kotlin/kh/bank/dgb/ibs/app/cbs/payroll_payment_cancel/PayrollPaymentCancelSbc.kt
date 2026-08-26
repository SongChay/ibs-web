package kh.bank.dgb.ibs.app.cbs.payroll_payment_cancel

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class PayrollPaymentCancelSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	// TODO: opcode unverified — see the doc comment on PayrollPaymentCancelCbc.
	fun cancel(request: RequestData<PayrollPaymentCancelRequest>): ResponseData<PayrollPaymentCancelResponse> {
		val result = coreBankingApiConnector.post("PYR05300232", request.header?.languageCode, request.body, PayrollPaymentCancelResponse::class.java)
		val body = result.body ?: return result

		val resultYn = if (result.header?.result == true) "Y" else "N"
		return ResponseData(header = result.header, body = body.copy(resultYn = resultYn))
	}
}
