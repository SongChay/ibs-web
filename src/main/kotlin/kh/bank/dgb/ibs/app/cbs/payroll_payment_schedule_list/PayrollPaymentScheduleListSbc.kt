package kh.bank.dgb.ibs.app.cbs.payroll_payment_schedule_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class PayrollPaymentScheduleListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun list(request: RequestData<PayrollPaymentScheduleListRequest>): ResponseData<PayrollPaymentScheduleListResponse> {
		val result = connector.post("CIB11300211", request.header?.languageCode, request.body, PayrollPaymentScheduleListResponse::class.java)
		val body = result.body
		if (result.header?.result != true || body == null) return result

		val updated = body.copy(
			salaryTransferFeeAmount = body.salaryTransferFeeAmount?.setScale(2),
			registerDate = formatDate(body.registerDate),
			unregisterDate = formatDate(body.unregisterDate),
			payrollPaymentScheduleList = body.payrollPaymentScheduleList?.map { item ->
				item.copy(
					salaryTransferAmount = item.salaryTransferAmount?.setScale(2),
					salaryTransferTotalFeeAmount = item.salaryTransferTotalFeeAmount?.setScale(2),
					salaryTransferExecutionDate = formatDate(item.salaryTransferExecutionDate),
				)
			},
		)
		return ResponseData(header = result.header, body = updated)
	}

	/** Port of `DateUtil.toDDMMMYYYY` — parses `yyyyMMdd`, formats as `dd MMM yyyy`; leaves
	 *  null/blank untouched, returns "" if parsing fails, same as the old util. */
	private fun formatDate(date: String?): String? {
		if (date.isNullOrEmpty()) return date
		return runCatching {
			val parsed = SimpleDateFormat("yyyyMMdd").parse(date)
			SimpleDateFormat("dd MMM yyyy").format(parsed as Date)
		}.getOrDefault("")
	}
}
