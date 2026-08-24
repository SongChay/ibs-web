package kh.bank.dgb.ibs.app.cbs.edc_balance_inquiry

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdcBalanceInquiryRequest(
	val receiverAccountNo: String? = null,
)

data class EdcBalanceInquiryResponse(
	val billerCode: String? = null,
	val billerName: String? = null,
	val companyCode: String? = null,
	val companyName: String? = null,
	val consumerCode: String? = null,
	val consumerName: String? = null,
	val consumerNameLatin: String? = null,
	val currency: String? = null,
	val totalAmount: String? = null,
	val billAmount: String? = null,
	val convenienceFeeAmount: String? = null,
	val sponsorFeeAmount: String? = null,
	val minAmount: String? = null,
	val maxAmount: String? = null,
	val paymentToken: String? = null,
	val lastBillDate: String? = null,
	val lastDueDate: String? = null,
	val descriptionKh: String? = null,
	val descriptionEn: String? = null,
	val feeAmount: Double? = null,
)

/** Port of `TRS0815_Adapter_inquiryEDCBalanceInquiry` — calls CBS opcode `CIB11300815`
 *  (via the old `DGBEBankingService.processCIB11300815`). Straight pass-through. */
@RestController
@RequestMapping("/TRS0815")
class EdcBalanceInquiryCbc(
	private val sbc: EdcBalanceInquirySbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<EdcBalanceInquiryRequest>): ResponseData<EdcBalanceInquiryResponse> =
		sbc.inquire(request)
}
