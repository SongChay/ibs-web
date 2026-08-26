package kh.bank.dgb.ibs.app.cbs.account_info_e_statement

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class AccountInfoEStatementRequest(
	val startDate: String? = null,
	val endDate: String? = null,
	val userID: String? = null,
	val accountNo: String? = null,
)

/** NOTE: old field name is literally `bICCode` (its Java getter is the non-standard `getbICCode()`,
 *  not `getBICCode()`), which Jackson resolves to a wire property name of `bICCode` — kept as-is
 *  here rather than "fixed" to `bicCode`, since this is a wire-contract field name. */
data class AccountInfoEStatementResponse(
	val bICCode: String? = null,
	val availableBalance: BigDecimal? = null,
	val totalCount: Double? = null,
	val endingBalance: BigDecimal? = null,
	val currencyCode: String? = null,
	val depositSubjectCode: String? = null,
	val depositAmount: BigDecimal? = null,
	val depositAccountStatusCode: String? = null,
	val accountName: String? = null,
	val accountNo: String? = null,
	val productName: String? = null,
	val address: String? = null,
	val withdrawalAmount: BigDecimal? = null,
	val openDate: String? = null,
	val beginningBalance: BigDecimal? = null,
	val startDate: String? = null,
	val customerNo: String? = null,
	val transactionCount: Long? = null,
	val endDate: String? = null,
)

/** Port of `TRS0914_Adapter_RetrieveAccountInfoEStatement` — calls CBS opcode `CIB11300914`
 *  (via the old `DGBEBankingService.processCIB11300914`). Straight pass-through. */
@RestController
@RequestMapping("/TRS0914")
class AccountInfoEStatementCbc(
	private val accountInfoEStatementSbc: AccountInfoEStatementSbc,
) {
	@PostMapping
	fun retrieve(@RequestBody request: RequestData<AccountInfoEStatementRequest>): ResponseData<AccountInfoEStatementResponse> {
		return accountInfoEStatementSbc.retrieve(request)
	}
}
