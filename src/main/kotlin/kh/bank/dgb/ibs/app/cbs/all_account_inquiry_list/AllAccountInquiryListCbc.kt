package kh.bank.dgb.ibs.app.cbs.all_account_inquiry_list

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Port of `ACI1006_REQ_AccountInquiryVo` (also reused verbatim by the old `ACI1002` adapter —
 *  see `AccountNoListSbc`). */
data class AllAccountInquiryListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val accountNo: String? = null,
	val accountTypeCode: String? = null,
	val channelTypeCode: String? = null,
	val currencyCode: String? = null,
	val accountNickName: String? = null,
)

/** Port of `ACI1006_RES_AccountInquiryListVo`. `transactionDate`/`transactionTime` are
 *  write-only (`Access.WRITE_ONLY` in the old Vo): populated from the CBS response, consumed to
 *  compute `transactionDateTime`, never serialized back out to the client. */
data class AccountInquiryListItem(
	val accountNo: String? = null,
	val productName: String? = null,
	val loanAmount: java.math.BigDecimal? = null,
	val applyInterestRate: java.math.BigDecimal? = null,
	val installmentsMonth: Long? = null,
	val paymentDay: String? = null,
	val repaymentMethodCode: String? = null,
	val repaymentMethodDescription: String? = null,
	val accountName: String? = null,
	val openDate: String? = null,
	val closeDate: String? = null,
	val branchCode: String? = null,
	val branchName: String? = null,
	val balance: java.math.BigDecimal? = null,
	val currencyCode: String? = null,
	val accountNickName: String? = null,
	val depositSubjectCode: String? = null,
	val depositSubjectDescription: String? = null,
	val depositAccountStatusCode: String? = null,
	val depositAccountStatusDescription: String? = null,
	val transactionDateTime: String? = null,
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	val transactionDate: String? = null,
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	val transactionTime: String? = null,
	val maturityDate: String? = null,
	val totalInterestAmount: java.math.BigDecimal? = null,
	val totalRepaymentAmount: java.math.BigDecimal? = null,
)

/** Port of `ACI1006_RES_WrapperAccountInquiryListVo` — client-facing key `accountList`, CBS wire
 *  key `grid01` accepted via `@JsonAlias` on the way in. */
data class AllAccountInquiryListResponse(
	@JsonAlias("grid01")
	val accountList: List<AccountInquiryListItem>? = null,
)

/**
 * Port of `ACI1006_Adapter_AllAccountInquiryList` — calls CBS opcode `CIB11300612` (via the old
 * `DGBEBankingService.getAccountList`, itself wrapping `processACC0002`).
 *
 * NOT a plain pass-through: `getAccountList` forces `accountTypeCode` to `"00"` in the CBS
 * request, then filters the result client-side against the original request values, and this
 * adapter additionally enriches each surviving row with three code-table descriptions and a
 * combined transaction date/time. All of that lives in `AllAccountInquiryListSbc` — see its
 * doc comments for the filter rules and the reused-by-`ACI1002` note.
 */
@RestController
@RequestMapping("/ACI1006")
class AllAccountInquiryListCbc(
	private val sbc: AllAccountInquiryListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<AllAccountInquiryListRequest>): ResponseData<AllAccountInquiryListResponse> =
		sbc.inquire(request)
}
