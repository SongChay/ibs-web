package kh.bank.dgb.ibs.app.cbs.inquiry_exchange_rate

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat

@Service
class InquiryExchangeRateSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	private val logger = LoggerFactory.getLogger(InquiryExchangeRateSbc::class.java)

	fun inquire(
		request: RequestData<InquiryExchangeRateRequest>,
	): ResponseData<InquiryExchangeRateResponse> {
		val result = coreBankingApiConnector.post(
			"CIB11000404",
			request.header?.languageCode,
			request.body,
			InquiryExchangeRateResponse::class.java,
		)

		val body = result.body ?: return result
		val reformattedDate = body.transactionDate?.let { toDisplayDate(it) }
		return result.copy(body = body.copy(transactionDate = reformattedDate))
	}

	/** Port of `DateUtil.toDDMMMYYYYHHMMSSA`, applied (as the old adapter did) to the CBS
	 *  `transactionDate` after stripping stray `[`/`]` characters. Expects `yyyy-MM-dd HH:mm:ss`
	 *  input; returns `dd MMM yyyy, hh:mm:ss a`, or `""` if blank/unparsable — matching the old
	 *  code's swallow-and-return-empty-string behaviour exactly. */
	private fun toDisplayDate(rawDate: String): String {
		val cleaned = rawDate.replace(Regex("[\\[\\]]"), "")
		if (cleaned.isBlank()) {
			return ""
		}

		val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a")
		return try {
			outputFormat.format(inputFormat.parse(cleaned))
		} catch (e: ParseException) {
			logger.warn("Failed to parse CBS exchange rate transactionDate: {}", rawDate, e)
			""
		}
	}
}
