package kh.bank.dgb.ibs.app.cbs.account_inquiry_detail

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class AccountInquiryDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<AccountInquiryDetailRequest>): ResponseData<AccountInquiryDetailResponse> {
		val result = coreBankingApiConnector.post("CIB11000711", request.header?.languageCode, request.body, AccountInquiryDetailResponse::class.java)

		val body = result.body ?: return result
		val reformatted = body.transactionList?.map { item ->
			item.copy(
				transactionDate = if ((item.transactionDate?.length ?: 0) > 1) toDDMMMYYYY(item.transactionDate) else item.transactionDate,
				transactionAmount = item.transactionAmount?.setScale(2),
				afterBalance = item.afterBalance?.setScale(2),
			)
		}

		return ResponseData(header = result.header, body = body.copy(transactionList = reformatted))
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
}
