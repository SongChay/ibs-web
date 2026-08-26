package kh.bank.dgb.ibs.app.cbs.edc_subscription_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class EdcSubscriptionListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val pageSize: Long? = null,
	val pageNumber: String? = null,
)

data class EdcSubscriptionListItem(
	val counterpartAccountNo: String? = null,
	val customerNo: String? = null,
	val billerID: String? = null,
	val templateName: String? = null,
	val subscriptionReferenceNo: String? = null,
	val subscriptionDate: String? = null,
	val subscriptionTime: String? = null,
	val subscriptionExpiryDate: String? = null,
	val accountNo: String? = null,
	val billCurrency: String? = null,
	val billDate: String? = null,
	val billDueDate: String? = null,
	val limitAmount: BigDecimal? = null,
	val lastPaymentDate: String? = null,
	val lastInvoiceID: String? = null,
	val firstSubscriptionDate: String? = null,
	val counterpartDetails: String? = null,
)

data class EdcSubscriptionListResponse(
	val grid01Count: Long? = null,
	val grid01: List<EdcSubscriptionListItem>? = null,
)

/** Port of `TRS2512_Adapter_RetrieveSubscriptionListEDC` — calls CBS opcode `CIB11102512` (via the
 *  old `DGBEBankingService.processCIB11102512`). */
@RestController
@RequestMapping("/TRS2512")
class EdcSubscriptionListCbc(
	private val edcSubscriptionListSbc: EdcSubscriptionListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<EdcSubscriptionListRequest>): ResponseData<EdcSubscriptionListResponse> {
		return edcSubscriptionListSbc.inquire(request)
	}
}
