package kh.bank.dgb.ibs.app.cbs.transaction_loan_account_details

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class TransactionLoanAccountDetailsRequest(
	val accountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val currentPage: Int? = null,
	val pageSize: Int? = null,
)

/** Port of `ACI2004_RES_TransactionLoanAccountDetailsVo`. */
data class TransactionLoanAccountDetailsItem(
	val paymentStatusCode: String? = null,
	val paymentStatusDescription: String? = null,
	val paymentDay: String? = null,
	val paymentDate: String? = null,
	val transactionAmount: java.math.BigDecimal? = null,
	val principalAmount: java.math.BigDecimal? = null,
	val interestRate: java.math.BigDecimal? = null,
	val paidPrincipalAmount: java.math.BigDecimal? = null,
	val paidInterestRate: java.math.BigDecimal? = null,
	val otherAmount: java.math.BigDecimal? = null,
	val remark: String? = null,
)

/** Port of `ACI2004_RES_TransactionLoanAccountDetailsListVo` — client-facing key `accountList`,
 *  CBS wire key `grid01` accepted via `@JsonAlias` on the way in. */
data class TransactionLoanAccountDetailsResponse(
	val totalCount: Long? = null,
	@JsonAlias("grid01")
	val accountList: List<TransactionLoanAccountDetailsItem>? = null,
)

/**
 * Port of `ACI2004_Adapter_RetrieveTransactionLoanAccountDetails` — calls CBS opcode
 * `CIB11300711` (via the old `DGBEBankingService.processCIB11300711`).
 *
 * NOT a plain pass-through: filters `accountList` down to rows whose `paymentDate` falls between
 * the request's `fromDate`/`toDate` (compared as numbers, inclusive on both ends), fills in
 * `paymentStatusDescription` for the surviving rows, and recomputes `totalCount` as the filtered
 * count — see `TransactionLoanAccountDetailsSbc`.
 */
@RestController
@RequestMapping("/ACI2004")
class TransactionLoanAccountDetailsCbc(
	private val sbc: TransactionLoanAccountDetailsSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<TransactionLoanAccountDetailsRequest>): ResponseData<TransactionLoanAccountDetailsResponse> =
		sbc.inquire(request)
}
