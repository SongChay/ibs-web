package kh.bank.dgb.ibs.app.cbs.payment_history

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PaymentHistoryRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val accountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val sortBy: String? = null,
	val currentPage: Int? = null,
	val pageSize: Int? = null,
)

data class AccountTransactionDetailHistoryItem(
	val user: String? = null,
	val date: String? = null,
	val time: String? = null,
	val status: String? = null,
)

data class PaymentHistoryItem(
	// 001 : Processing, 002 : Failed, 003 : Completed, 000 : Unknown
	val transactionStatusCode: String? = null,
	val transactionStatusDescription: String? = null,
	val transferTypeCode: String? = null,
	val transferTypeDescription: String? = null,
	val transactionTypeCode: String? = null,
	val transactionTypeDescription: String? = null,
	val requestDate: String? = null,
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val transactionDateTime: String? = null,
	val withdrawalAccountNo: String? = null,
	val accountHolder: String? = null,
	val accountNickname: String? = null,
	val senderAddress: String? = null,
	val receiverAccountNo: String? = null,
	val receiverName: String? = null,
	val transactionAmount: BigDecimal? = null,
	val transactionFeeAmount: BigDecimal? = null,
	val transactionCurrencyCode: String? = null,
	val receiverAccountRemark: String? = null,
	val transactionErrorDetailCode: String? = null,
	val purposeOfRequestDescription: String? = null,
	val withdrawalAccountRemark: String? = null,
	val eBankTransactionTypeCode: String? = null,
	val depositTransactionTypeCode: String? = null,
	val senderAccountName: String? = null,
	val senderAccountNo: String? = null,
	val slipNo: String? = null,
	val additionalTransactionContent: String? = null,
	val ourBankReferenceNo: String? = null,
	val counterpartReferenceNo: String? = null,
	val transactionSummary: String? = null,
	val approval: List<AccountTransactionDetailHistoryItem>? = null,
)

data class PaymentHistoryResponse(
	@JsonAlias("grid01Count") val totalCount: Long? = null,
	@JsonAlias("grid01") val paymentList: List<PaymentHistoryItem>? = null,
)

/** Port of `TRS1312_Adapter_InquiryPaymentHistory` — calls CBS opcode `CIB11301312` (via the old
 *  `DGBEBankingService.processCIB11301312`). */
@RestController
@RequestMapping("/TRS1312")
class PaymentHistoryCbc(
	private val sbc: PaymentHistorySbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<PaymentHistoryRequest>): ResponseData<PaymentHistoryResponse> =
		sbc.inquire(request)
}
