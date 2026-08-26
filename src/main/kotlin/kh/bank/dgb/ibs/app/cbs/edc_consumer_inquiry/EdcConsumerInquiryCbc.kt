package kh.bank.dgb.ibs.app.cbs.edc_consumer_inquiry

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdcConsumerInquiryRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val receiverAccountNo: String? = null,
)

data class EdcConsumerInquiryResponse(
	val companyCode: String? = null,
	val companyName: String? = null,
	val consumerCode: String? = null,
	val consumerName: String? = null,
	// NB: name kept exactly as the old Java field ("conumer", not "consumer") — this is a wire
	// contract, not free to fix the typo.
	val conumerNameLatin: String? = null,
)

/** Port of `TRS2511_Adapter_RetrieveConsumerInquiryEDC` — calls CBS opcode `CIB11102511` (via the
 *  old `DGBEBankingService.processCIB11102511`). */
@RestController
@RequestMapping("/TRS2511")
class EdcConsumerInquiryCbc(
	private val edcConsumerInquirySbc: EdcConsumerInquirySbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<EdcConsumerInquiryRequest>): ResponseData<EdcConsumerInquiryResponse> {
		return edcConsumerInquirySbc.inquire(request)
	}
}
