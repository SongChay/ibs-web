package kh.bank.dgb.ibs.app.cbs.payroll_payment_schedule_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class PayrollPaymentScheduleDetailSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun detail(request: RequestData<PayrollPaymentScheduleDetailRequest>): ResponseData<PayrollPaymentScheduleDetailResponse> =
		connector.post("CIB11300212", request.header?.languageCode, request.body, PayrollPaymentScheduleDetailResponse::class.java)
}
