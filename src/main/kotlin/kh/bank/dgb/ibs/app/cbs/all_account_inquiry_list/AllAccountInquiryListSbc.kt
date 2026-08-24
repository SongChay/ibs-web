package kh.bank.dgb.ibs.app.cbs.all_account_inquiry_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class AllAccountInquiryListSbc(
	private val connector: CoreBankingApiConnector,
) {

	/**
	 * Port of `DGBEBankingServiceImpl.getAccountList` — always calls opcode `CIB11300612`
	 * (`processACC0002`) with `accountTypeCode` forced to `"00"`, then filters the result
	 * client-side against the *original* (pre-override) `accountNo`/`accountNickName`/
	 * `accountTypeCode`/`currencyCode` values from the request.
	 *
	 * `internal` rather than `private` so `AccountNoListSbc` (`app.cbs.account_no_list`) can call
	 * straight into this instead of duplicating the filter — the old code funnelled both the
	 * ACI1002 and ACI1006 adapters through this one service method the same way.
	 *
	 * Deliberately does NOT do the description/date-time enrichment in [inquire] below — in the
	 * old code that enrichment lived in the ACI1006 adapter layer, not in `getAccountList` itself,
	 * and ACI1002 never picked it up (it only projects `accountNo`/`accountName`/
	 * `accountNickName`/`currencyCode` out of the raw row anyway).
	 */
	internal fun fetchFilteredAccounts(request: RequestData<AllAccountInquiryListRequest>): ResponseData<AllAccountInquiryListResponse> {
		val originalBody = request.body
		val filterAccountNo = (originalBody?.accountNo ?: "").replace("-", "")
		val filterAccountNickName = (originalBody?.accountNickName ?: "").lowercase()
		val filterAccountTypeCode = originalBody?.accountTypeCode
		val filterCurrencyCode = originalBody?.currencyCode

		val cbsRequestBody = originalBody?.copy(accountTypeCode = "00")
		val cbsResult = connector.post("CIB11300612", request.header?.languageCode, cbsRequestBody, AllAccountInquiryListResponse::class.java)

		val filtered = cbsResult.body?.accountList?.filter { item ->
			matchesFilter(item, filterAccountNo, filterAccountNickName, filterAccountTypeCode, filterCurrencyCode)
		} ?: emptyList()

		return ResponseData(header = cbsResult.header, body = AllAccountInquiryListResponse(accountList = filtered))
	}

	/** The old code's eight `if`/`else if` branches, collapsed into one boolean expression — same
	 *  three conditions (account-type match, account-no-or-nickname match, currency match) ORed
	 *  across "empty filter" vs "actual match" for each. */
	private fun matchesFilter(
		item: AccountInquiryListItem,
		filterAccountNo: String,
		filterAccountNickName: String,
		filterAccountTypeCode: String?,
		filterCurrencyCode: String?,
	): Boolean {
		// Java's `.substring(3)` throws below length 3; `.drop(3)` degrades to "" instead — the
		// only behavioral difference, and only for malformed (<3 char) account numbers.
		val itemAccountNo = (item.accountNo ?: "").drop(3)
		val itemAccountNickName = (item.accountNickName ?: "").lowercase()

		val accountTypeCodeEmpty = filterAccountTypeCode.isNullOrEmpty()
		val accountTypeCodeMatches = item.depositSubjectCode.equals(filterAccountTypeCode, ignoreCase = true)

		val accountNoOrNickNameEmpty = filterAccountNo.isEmpty() || filterAccountNickName.isEmpty()
		val accountNoOrNickNameMatches = itemAccountNo.contains(filterAccountNo) || itemAccountNickName.contains(filterAccountNickName)

		val currencyCodeEmpty = filterCurrencyCode.isNullOrEmpty()
		val currencyCodeMatches = item.currencyCode.equals(filterCurrencyCode, ignoreCase = true)

		// The old code's eight branches are exactly this AND of three "empty-or-matches" gates —
		// each if/else-if pairing covers one of the eight (typeCode x accountNoOrNickName x
		// currency) combinations, all producing the same "add to result" action.
		return (accountTypeCodeEmpty || accountTypeCodeMatches) &&
			(accountNoOrNickNameEmpty || accountNoOrNickNameMatches) &&
			(currencyCodeEmpty || currencyCodeMatches)
	}

	/** Port of `ACI1006_Adapter_AllAccountInquiryList.process` — [fetchFilteredAccounts] plus the
	 *  code-table description lookups and combined transaction date/time, applied only when the
	 *  CBS call itself succeeded (`header.result == true`), exactly as the old adapter guarded it. */
	fun inquire(request: RequestData<AllAccountInquiryListRequest>): ResponseData<AllAccountInquiryListResponse> {
		val result = fetchFilteredAccounts(request)
		if (result.header?.result != true) {
			return result
		}

		val enriched = result.body?.accountList?.map(::enrich)
		return ResponseData(header = result.header, body = AllAccountInquiryListResponse(accountList = enriched))
	}

	private fun enrich(item: AccountInquiryListItem): AccountInquiryListItem {
		// TODO: the old code guarded this with `transactionDate != "" && transactionTime != ""`,
		// which is Java *reference* inequality on possibly-deserialized strings, not a content
		// check — in practice that guard is true for virtually all values (null included) since
		// they are almost never the same interned "" instance, so the parse-and-catch always ran.
		// Reproduced here as a sane null/empty check instead of the reference-equality bug.
		val transactionDateTime = if (!item.transactionDate.isNullOrEmpty() && !item.transactionTime.isNullOrEmpty()) {
			runCatching {
				val parsed = SimpleDateFormat("yyyyMMddHHmmssSSS").parse(item.transactionDate + item.transactionTime)
				SimpleDateFormat("dd MMM yyyy, hh:mm:ss a").format(parsed)
			}.getOrNull()
		} else {
			null
		}

		return item.copy(
			depositSubjectDescription = depositSubjectDescription(item.depositSubjectCode),
			depositAccountStatusDescription = depositAccountStatusDescription(item.depositAccountStatusCode),
			repaymentMethodDescription = repaymentMethodDescription(item.repaymentMethodCode),
			transactionDateTime = transactionDateTime ?: item.transactionDateTime,
		)
	}

	/** Port of `DataUtils.getDepositSubjectDescription` / `type.DepositSubjectCode`. */
	private fun depositSubjectDescription(code: String?): String = when (code) {
		"110" -> "Current Account"
		"120" -> "Saving Account"
		"130" -> "Fixed Account"
		"140" -> "Installment Account"
		"150" -> "Loan Account"
		else -> ""
	}

	/** Port of `DataUtils.getDepositAccountStatusDescription` / `type.DepositAccountStatusCode`. */
	private fun depositAccountStatusDescription(code: String?): String = when (code) {
		"01" -> "Normal"
		"04" -> "Transaction Suspended"
		"06" -> "Blocked"
		"07" -> "Normally Terminated"
		"09" -> "Terminated with other reason"
		"10" -> "LumpsumLedger"
		"21" -> "Terminated on maturity"
		"22" -> "Early Termination"
		"32" -> "Netting on maturity"
		"40" -> "Terminated due to Branch transfer"
		"50" -> "Account closed"
		"99" -> "New registratoin cancelled"
		else -> ""
	}

	/** Port of `DataUtils.getRepaymentMethodDescription` / `type.RepaymentMethodCode`. */
	private fun repaymentMethodDescription(code: String?): String = when (code) {
		"10" -> "Bullet"
		"20" -> "Installment"
		"30" -> "Amortization"
		"40" -> "Negotiable"
		"90" -> "Other(OD)"
		else -> ""
	}
}
