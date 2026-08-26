package kh.bank.dgb.ibs.app.cbs.retrieve_top3_transaction_history

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RetrieveTop3TransactionHistoryRequest(
	val userID: String? = null,
	val accountNo: String? = null,
)

data class RetrieveTop3TransactionHistoryItem(
	val transactionTypeDescription: String? = null,
	val crOrDr: String? = null,
	val receiverBankCode: String? = null,
	val receiverBankName: String? = null,
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val ownerAccountNo: String? = null,
	val ownerAccoutName: String? = null,
	val accountNickName: String? = null,
	val phoneNumber: String? = null,
	val receiverAccountNo: String? = null,
	val transactionAmount: java.math.BigDecimal? = null,
	val currencyCode: String? = null,
	val receiverAddress: String? = null,
	val receiverName: String? = null,
	val successCount: Int? = null,
	val transferCount: Int? = null,
)

data class RetrieveTop3TransactionHistoryResponse(
	@JsonAlias("grid01") val transactionList: List<RetrieveTop3TransactionHistoryItem>? = null,
)

/** Port of `MAN1006_Adapter_RetrieveTop3TransactionHistory` — calls CBS opcode `CIB11300415`
 *  (via the old `DGBEBankingService.processTRN0112`). */
@RestController
@RequestMapping("/MAN1006")
class RetrieveTop3TransactionHistoryCbc(
	private val retrieveTop3TransactionHistorySbc: RetrieveTop3TransactionHistorySbc,
) {
	@PostMapping
	fun retrieve(
		@RequestBody request: RequestData<RetrieveTop3TransactionHistoryRequest>,
	): ResponseData<RetrieveTop3TransactionHistoryResponse> {
		return retrieveTop3TransactionHistorySbc.retrieve(request)
	}
}
