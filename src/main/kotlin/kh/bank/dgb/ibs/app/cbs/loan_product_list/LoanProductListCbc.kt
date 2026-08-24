package kh.bank.dgb.ibs.app.cbs.loan_product_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Port of `ADS4001_RES_InquiryLoanProductVo`. */
data class LoanProductListItem(
	val productName: String? = null,
	val productNameKH: String? = null,
	val interestRate: String? = null,
	val productCode: String? = null,
	val currencyCode: String? = null,
	val loanMinAmount: String? = null,
	val loanMaxAmount: String? = null,
	val loanAmount: String? = null,
	val tenor: String? = null,
)

/** Port of `ADS4001_RES_WrapperInquiryLoanProductListVo` — client-facing keys `loanProductCount`/
 *  `loanProductList`, CBS wire keys `grid01Count`/`grid01` accepted via `@JsonAlias` on the way in
 *  (the old Vo used a `@JsonProperty` split between getter and setter for the same effect). */
data class LoanProductListResponse(
	@JsonAlias("grid01Count")
	val loanProductCount: Long? = null,
	@JsonAlias("grid01")
	val loanProductList: List<LoanProductListItem>? = null,
)

/**
 * Port of `ADS4001_Adapter_InquiryLoanProductList` — calls CBS opcode `CIB11003811` (via the old
 * `DGBEBankingService.processCIB11003811`).
 *
 * HYBRID adapter: after the CBS call, every returned loan product's `productNameKH` is
 * overwritten from the *local* `BbsBoardProductService.getLoanProductNameKH(productCode)` lookup
 * (a DB read, not part of the CBS response) — ported here as a call into the existing
 * `BbsBoardProductRbc` (`app.local.bbs_board_product`), reused as instructed rather than
 * recreated. See `LoanProductListSbc`.
 */
@RestController
@RequestMapping("/ADS4001")
class LoanProductListCbc(
	private val sbc: LoanProductListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<Unit>): ResponseData<LoanProductListResponse> =
		sbc.inquire(request)
}
