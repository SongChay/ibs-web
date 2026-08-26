package kh.bank.dgb.ibs.app.cbs.edc_subscription_list_inquiry

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdcSubscriptionListInquiryRequest(
	val userID: String? = null,
	val eBankTransactionTypeCode: String? = null,
	val pageSize: Long? = null,
	val pageNumber: String? = null,
	val accountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val sortBy: String? = null,
)

data class EdcSubscriptionListInquiryItem(
	val transactionStatusCode: String? = null,
	val transactionStatusCodeDescription: String? = null,
	val eBankTransactionTypeCode: String? = null,
	val eBankTransactionTypeCodeDescription: String? = null,
	val seqNo: Int? = null,
	val transferTypeCode: String? = null,
	val transferTypeCodeDescription: String? = null,
	val requestDate: String? = null,
	val requestTime: String? = null,
	val requestDateAndTime: String? = null,
	val withdrawalAccountNo: String? = null,
	val accountHolder: String? = null,
	val accountNickname: String? = null,
	val receiverAccountNo: String? = null,
	val receiverName: String? = null,
	val approvalNo: Int? = null,
	val createDateTime: String? = null,
	val senderAccountNo: String? = null,
	val senderAccountName: String? = null,
	val transactionDateFormat: String? = null,
	val subscriptionReferenceNo: String? = null,
	val templateName: String? = null,
	val requester: String? = null,
)

data class EdcSubscriptionListInquiryResponse(
	val grid01Count: Long? = null,
	val grid01: List<EdcSubscriptionListInquiryItem>? = null,
)

/** Port of `TRS2514_Adapter_RetrieveSubscriptionListInquiryEDC` — calls CBS opcode `CIB11102514`
 *  (via the old `DGBEBankingService.processCIB11102514`). */
@RestController
@RequestMapping("/TRS2514")
class EdcSubscriptionListInquiryCbc(
	private val edcSubscriptionListInquirySbc: EdcSubscriptionListInquirySbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<EdcSubscriptionListInquiryRequest>): ResponseData<EdcSubscriptionListInquiryResponse> {
		return edcSubscriptionListInquirySbc.inquire(request)
	}
}
