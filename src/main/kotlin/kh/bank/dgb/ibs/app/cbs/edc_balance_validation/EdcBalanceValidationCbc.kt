package kh.bank.dgb.ibs.app.cbs.edc_balance_validation

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdcBalanceValidationRequest(
	val transactionAmount: Double? = null,
	val paymentToken: String? = null,
)

data class EdcBalanceValidationResponse(
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
	val paymentToken: String? = null,
	val lastBillDate: String? = null,
	val lastDueDate: String? = null,
	val descriptionKh: String? = null,
	val descriptionEn: String? = null,
	val resultYN: String? = null,
)

/** Port of `TRS0816_Adapter_inquiryEDCBalanceValidation` — calls CBS opcode `CIB11300816`
 *  (via the old `DGBEBankingService.processCIB11300816`). Straight pass-through. */
@RestController
@RequestMapping("/TRS0816")
class EdcBalanceValidationCbc(
	private val sbc: EdcBalanceValidationSbc,
) {
	@PostMapping
	fun validate(@RequestBody request: RequestData<EdcBalanceValidationRequest>): ResponseData<EdcBalanceValidationResponse> =
		sbc.validate(request)
}
