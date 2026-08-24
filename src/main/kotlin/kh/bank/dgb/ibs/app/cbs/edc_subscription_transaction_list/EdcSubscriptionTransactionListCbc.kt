package kh.bank.dgb.ibs.app.cbs.edc_subscription_transaction_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class EdcSubscriptionTransactionListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val pageSize: Long? = null,
	val pageNumber: String? = null,
	val counterpartAccountNo: String? = null,
	val accountNo: String? = null,
	val startDate: String? = null,
	val endDate: String? = null,
)

data class EdcSubscriptionTransactionListItem(
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val transactionTypeCode: String? = null,
	val transactionAmount: BigDecimal? = null,
	val transactionCurrencyCode: String? = null,
	val ourBankReferenceNo: String? = null,
	val statusCode: String? = null,
	val accountName: String? = null,
	val accountNo: String? = null,
	val counterpartAccountNo: String? = null,
	val counterpartName: String? = null,
	val counterpartReferenceNo: String? = null,
	val remark: String? = null,
)

data class EdcSubscriptionTransactionListResponse(
	val grid01Count: Long? = null,
	val grid01: List<EdcSubscriptionTransactionListItem>? = null,
)

/** Port of `TRS2513_Adapter_RetrieveSubscriptionTransactionListEDC` — calls CBS opcode
 *  `CIB11102513` (via the old `DGBEBankingService.processCIB11102513`). */
@RestController
@RequestMapping("/TRS2513")
class EdcSubscriptionTransactionListCbc(
	private val sbc: EdcSubscriptionTransactionListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<EdcSubscriptionTransactionListRequest>): ResponseData<EdcSubscriptionTransactionListResponse> =
		sbc.inquire(request)
}
