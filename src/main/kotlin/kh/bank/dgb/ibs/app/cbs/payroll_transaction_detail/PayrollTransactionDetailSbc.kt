package kh.bank.dgb.ibs.app.cbs.payroll_transaction_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class PayrollTransactionDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun detail(request: RequestData<PayrollTransactionDetailRequest>): ResponseData<PayrollTransactionDetailResponse> {
		val result = coreBankingApiConnector.post("CIB11300915", request.header?.languageCode, request.body, PayrollTransactionDetailResponse::class.java)
		val body = result.body ?: return result

		val updated = body.copy(
			totalAmount = body.totalAmount?.setScale(2),
			totalFee = body.totalFee?.setScale(2),
			transferDate = formatDate(body.transferDate),
			grid01 = body.grid01?.map { item -> item.copy(transferAmount = item.transferAmount?.setScale(2)) },
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
}
