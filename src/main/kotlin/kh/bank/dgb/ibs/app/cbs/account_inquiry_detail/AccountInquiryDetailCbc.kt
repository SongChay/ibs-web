package kh.bank.dgb.ibs.app.cbs.account_inquiry_detail

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AccountInquiryDetailRequest(
	val accountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val depositTransactionTypeCode: String? = null,
	val currentPage: Int? = null,
	val pageSize: Int? = null,
)

/** Port of `ACI2003_RES_AccountInquiryDetailListVo`. */
data class AccountInquiryDetailItem(
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val depositTransactionTypeCode: String? = null,
	val transactionCode: String? = null,
	val transactionDescription: String? = null,
	val branchCode: String? = null,
	val branchName: String? = null,
	val tellerID: String? = null,
	val transactionAmount: java.math.BigDecimal? = null,
	val afterBalance: java.math.BigDecimal? = null,
	val crOrDr: String? = null,
	val currencyCode: String? = null,
	val transactionReasonDesc: String? = null,
	val approvalUserName: String? = null,
	val slipNo: String? = null,
)

/** Port of `ACI2003_RES_WrapperAccountInquiryDetailListVo` — client-facing key `transactionList`,
 *  CBS wire key `grid01` accepted via `@JsonAlias` on the way in. */
data class AccountInquiryDetailResponse(
	val totalCount: Long? = null,
	val filterCount: Int? = null,
	@JsonAlias("grid01")
	val transactionList: List<AccountInquiryDetailItem>? = null,
)

/**
 * Port of `ACI2003_Adapter_AccountInquiryDetail` — calls CBS opcode `CIB11000711` (via the old
 * `DGBEBankingService.processACC0013`).
 *
 * NOT a plain pass-through: when the response body is present, every transaction row gets its
 * `transactionDate` reformatted (`yyyyMMdd` -> `dd MMM yyyy`, only when longer than 1 char) and
 * both `transactionAmount`/`afterBalance` rescaled to 2 decimal places — see
 * `AccountInquiryDetailSbc`.
 */
@RestController
@RequestMapping("/ACI2003")
class AccountInquiryDetailCbc(
	private val sbc: AccountInquiryDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<AccountInquiryDetailRequest>): ResponseData<AccountInquiryDetailResponse> =
		sbc.inquire(request)
}
