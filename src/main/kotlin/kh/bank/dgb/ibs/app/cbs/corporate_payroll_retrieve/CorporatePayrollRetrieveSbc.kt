package kh.bank.dgb.ibs.app.cbs.corporate_payroll_retrieve

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class CorporatePayrollRetrieveSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun retrieve(request: RequestData<CorporatePayrollRetrieveRequest>): ResponseData<CorporatePayrollRetrieveResponse> {
		val result = coreBankingApiConnector.post("CIB11300111", request.header?.languageCode, request.body, CorporatePayrollRetrieveResponse::class.java)
		val body = result.body ?: return result

		val updated = body.copy(
			registerDate = formatDate(body.registerDate),
			unregisterDate = formatDate(body.unregisterDate),
			salaryTransferFeeAmount = body.salaryTransferFeeAmount?.setScale(2),
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
