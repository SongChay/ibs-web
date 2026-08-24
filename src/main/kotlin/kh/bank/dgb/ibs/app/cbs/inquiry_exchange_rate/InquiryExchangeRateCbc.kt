package kh.bank.dgb.ibs.app.cbs.inquiry_exchange_rate

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class InquiryExchangeRateRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
)

data class InquiryExchangeRateItem(
	val transactionDate: String? = null,
	val currencyCode: String? = null,
	val buyingRate: java.math.BigDecimal? = null,
	val sellingRate: java.math.BigDecimal? = null,
	val spotRate: java.math.BigDecimal? = null,
	val avgRate: java.math.BigDecimal? = null,
)

data class InquiryExchangeRateResponse(
	val transactionDate: String? = null,
	@JsonAlias("grid01") val exchangeRateList: List<InquiryExchangeRateItem>? = null,
)

/** Port of `MAN1008_Adapter_InquiryExchangeRate` — calls CBS opcode `CIB11000404`
 *  (via the old `DGBEBankingService.processMIS0001`).
 *
 *  Real logic beyond a plain pass-through: the old adapter strips stray `[`/`]` characters from
 *  the CBS `transactionDate` and reformats it from `yyyy-MM-dd HH:mm:ss` to
 *  `dd MMM yyyy, hh:mm:ss a` (old `DateUtil.toDDMMMYYYYHHMMSSA`). Replicated in the Sbc. */
@RestController
@RequestMapping("/MAN1008")
class InquiryExchangeRateCbc(
	private val sbc: InquiryExchangeRateSbc,
) {
	@PostMapping
	fun inquire(
		@RequestBody request: RequestData<InquiryExchangeRateRequest>,
	): ResponseData<InquiryExchangeRateResponse> = sbc.inquire(request)
}
