package kh.bank.dgb.ibs.app.cbs.account_transaction_detail

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Port of `ACI1008_REQ_InquiryAccountTransationDetailVo` (old class name keeps its "Transation"
 *  typo; fixed here). */
data class AccountTransactionDetailRequest(
	val customerNo: String? = null,
	val accountNo: String? = null,
	val depositTransactionTypeCode: String? = null,
	val transactionDate: String? = null,
	val slipNo: String? = null,
)

/** Port of `ACI1008_RES_InquiryAccountTransationDetailVo` — one approval-history row. */
data class AccountTransactionDetailApproval(
	val user: String? = null,
	val date: String? = null,
	val time: String? = null,
	val status: String? = null,
)

/** Port of `ACI1008_RES_WrapperInquiryAccountTransationDetailVo`. */
data class AccountTransactionDetailResponse(
	val transactionTitle: String? = null,
	val debitCreditTypeCode: String? = null,
	val accountNo: String? = null,
	val accountName: String? = null,
	val receiverAccountNo: String? = null,
	val receiverAccountName: String? = null,
	val currencyCode: String? = null,
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val transactionDateTime: String? = null,
	val transactionAmount: String? = null,
	val receiverBankCode: String? = null,
	val receiverBankName: String? = null,
	val receiverAccountRemark: String? = null,
	val slipNo: String? = null,
	val customerNo: String? = null,
	val senderAccountNo: String? = null,
	val senderAccountName: String? = null,
	val senderBankCode: String? = null,
	val senderBankName: String? = null,
	val withdrawalAccountRemark: String? = null,
	val purpose: String? = null,
	val transactionSummary: String? = null,
	val eBankTransactionTypeCode: String? = null,
	val additionalTransactionContent: String? = null,
	val approval: List<AccountTransactionDetailApproval>? = null,
	val counterpartReferenceNo: String? = null,
)

/**
 * Port of `ACI1008_Adapter_InquiryAccountTransationDetail` — calls CBS opcode `CIB11000712` (via
 * the old `DGBEBankingService.processCIB11000712`).
 *
 * NOT a plain pass-through: when the CBS call succeeds, every `approval` row's `date`/`time` is
 * reformatted (`yyyyMMdd` -> `dd MMM yyyy`, `HHmm` -> `hh:mm a`) — see `AccountTransactionDetailSbc`.
 */
@RestController
@RequestMapping("/ACI1008")
class AccountTransactionDetailCbc(
	private val accountTransactionDetailSbc: AccountTransactionDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<AccountTransactionDetailRequest>): ResponseData<AccountTransactionDetailResponse> {
		return accountTransactionDetailSbc.inquire(request)
	}
}
