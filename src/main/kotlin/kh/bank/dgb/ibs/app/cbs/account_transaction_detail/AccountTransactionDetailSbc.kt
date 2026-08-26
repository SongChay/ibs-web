package kh.bank.dgb.ibs.app.cbs.account_transaction_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class AccountTransactionDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<AccountTransactionDetailRequest>): ResponseData<AccountTransactionDetailResponse> {
		val result = coreBankingApiConnector.post("CIB11000712", request.header?.languageCode, request.body, AccountTransactionDetailResponse::class.java)

		if (result.header?.result != true) {
			return result
		}

		val reformatted = result.body?.approval?.map { approval ->
			approval.copy(
				date = toDDMMMYYYY(approval.date),
				time = toHHMMA(approval.time),
			)
		}

		return ResponseData(header = result.header, body = result.body?.copy(approval = reformatted))
	}

	/** Port of `DateUtil.toDDMMMYYYY` (`yyyyMMdd` -> `dd MMM yyyy`), swallowing parse failures into
	 *  `""` exactly like the old code's caught-and-ignored `ParseException`. */
	private fun toDDMMMYYYY(date: String?): String {
		if (date.isNullOrBlank()) return ""
		return runCatching {
			val parsed = SimpleDateFormat("yyyyMMdd").parse(date)
			SimpleDateFormat("dd MMM yyyy").format(parsed)
		}.getOrDefault("")
	}

	/** Port of `DateUtil.toHHMMA` (`HHmm`, using only the first 4 characters -> `hh:mm a`). */
	private fun toHHMMA(time: String?): String {
		if (time.isNullOrBlank()) return ""
		return runCatching {
			val truncated = time.take(4)
			val parsed = SimpleDateFormat("HHmm").parse(truncated)
			SimpleDateFormat("hh:mm a").format(parsed)
		}.getOrDefault("")
	}
}
