package kh.bank.dgb.ibs.app.local.bbs_board_product

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class LoanProductDetailRequest(
	val productCode: String? = null,
)

/** Port of `ADS4001_RES_InquiryLoanProductVo`. Old adapter only ever populated
 *  `productName`/`productCode`/`interestRate` (the `productDescription` field was commented out,
 *  and `productNameKH`/`currencyCode`/`loanMinAmount`/`loanMaxAmount`/`loanAmount`/`tenor` were
 *  never set at all) — dropped here rather than carried over unused, same convention as
 *  `NewsEventCbc`'s dead-field drops. */
data class LoanProductDetailResponse(
	val productName: String? = null,
	val productCode: String? = null,
	val interestRate: String? = null,
)

data class TermConditionDetailRequest(
	val boardId: Int = 0,
)

/** Port of `ADS4201_RES_InquiryTermConditionVo`. */
data class TermConditionDetailResponse(
	val boardId: Int,
	val productNameEn: String? = null,
	val productNameKh: String? = null,
	val productDescEn: String? = null,
	val productDescKh: String? = null,
	val productInterestRate: String? = null,
)

/**
 * Ports of `ADS4101_Adapter_InquiryLoanProductDetail` and `ADS4201_Adapter_InquiryTermConditionDetail`
 * — both purely local, no CBS call, both backed by `BbsBoardProductRbc`. Grouped in one
 * controller/service pair since they share the same Rbc and feature folder. `ADS4001_Adapter_
 * InquiryLoanProductList` does NOT live here (checked — no list endpoint present in this file);
 * each of the two adapters actually present gets its own absolute route below (no class-level
 * `@RequestMapping`, since the two routes share no common prefix).
 */
@RestController
class BbsBoardProductCbc(
	private val sbc: BbsBoardProductSbc,
) {
	/** Port of `ADS4101_Adapter_InquiryLoanProductDetail`. */
	@PostMapping("/ADS4101")
	fun loanProductDetail(@RequestBody request: RequestData<LoanProductDetailRequest>): ResponseData<LoanProductDetailResponse> {
		val result = sbc.getLoanProductDetail(request.body?.productCode)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}

	/** Port of `ADS4201_Adapter_InquiryTermConditionDetail`. */
	@PostMapping("/ADS4201")
	fun termConditionDetail(@RequestBody request: RequestData<TermConditionDetailRequest>): ResponseData<TermConditionDetailResponse> {
		val boardId = request.body?.boardId ?: 0
		val result = sbc.getTermConditionDetail(boardId)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
