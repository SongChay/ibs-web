package kh.bank.dgb.ibs.app.cbs.payroll_payment_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class PayrollPaymentListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun list(request: RequestData<PayrollPaymentListRequest>): ResponseData<PayrollPaymentListResponse> {
		val result = connector.post("CIB11300911", request.header?.languageCode, request.body, PayrollPaymentListResponse::class.java)
		val body = result.body ?: return result

		val updated = body.copy(
			payrollPaymentList = body.payrollPaymentList?.map { item ->
				item.copy(
					salaryTransferAmount = item.salaryTransferAmount?.setScale(2),
					feeAmount = item.feeAmount?.setScale(2),
					firstRegisterDate = formatDate(item.firstRegisterDate),
					firstRegisterTime = formatTime(item.firstRegisterTime),
					salaryTransferExecutionDate = formatDate(item.salaryTransferExecutionDate),
					updateDate = formatDate(item.updateDate),
					updateTime = formatTime(item.updateTime),
				)
			},
		)
		return ResponseData(header = result.header, body = updated)
	}

	/** Port of `DateUtil.toDDMMMYYYY` — parses `yyyyMMdd`, formats as `dd MMM yyyy`. */
	private fun formatDate(date: String?): String? {
		if (date.isNullOrEmpty()) return date
		return runCatching {
			val parsed = SimpleDateFormat("yyyyMMdd").parse(date)
			SimpleDateFormat("dd MMM yyyy").format(parsed as Date)
		}.getOrDefault("")
	}

	/** Port of `DateUtil.toHHMMA` — takes the first 4 chars as `HHmm`, formats as `hh:mm a`. */
	private fun formatTime(time: String?): String? {
		if (time.isNullOrEmpty()) return time
		return runCatching {
			val parsed = SimpleDateFormat("HHmm").parse(time.take(4))
			SimpleDateFormat("hh:mm a").format(parsed as Date)
		}.getOrDefault("")
	}
}
