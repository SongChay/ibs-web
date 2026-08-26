package kh.bank.dgb.ibs.app.cbs.payroll_schedule_transfer_update

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class PayrollScheduleTransferUpdateSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<PayrollScheduleTransferUpdateRequest>): ResponseData<PayrollScheduleTransferUpdateResponse> {
		val result = coreBankingApiConnector.post(
			"CIB11300223",
			request.header?.languageCode,
			request.body,
			PayrollScheduleTransferUpdateResponse::class.java,
		)
		val body = result.body ?: return result

		val resultYn = if (result.header?.result == true) "Y" else "N"
		return ResponseData(header = result.header, body = body.copy(resultYn = resultYn))
	}
}
