package kh.bank.dgb.ibs.app.local.bbs_board_product

import org.springframework.stereotype.Service

@Service
class BbsBoardProductSbc(
	private val bbsBoardProductRbc: BbsBoardProductRbc,
) {
	/** Port of `ADS4101_Adapter_InquiryLoanProductDetail`. Returns an all-null response (not an
	 *  error) when the product code doesn't match anything, same as the old adapter. */
	fun getLoanProductDetail(productCode: String?): LoanProductDetailResponse {
		val product = productCode?.let { bbsBoardProductRbc.getLoanProductByProductCode(it) }
		return if (product != null) {
			LoanProductDetailResponse(
				productName = product.productNameEn,
				productCode = productCode,
				interestRate = product.productInterestRate,
			)
		} else {
			LoanProductDetailResponse()
		}
	}

	/** Port of `ADS4201_Adapter_InquiryTermConditionDetail`. Returns an all-null/zero response (not
	 *  an error) when the board id doesn't match anything, same as the old adapter — note that on a
	 *  miss `boardId` comes back `0`, not the requested id (old Java Vo's unset `int` default,
	 *  replicated faithfully rather than "fixed"). */
	fun getTermConditionDetail(boardId: Int): TermConditionDetailResponse {
		val product = bbsBoardProductRbc.getTermConditionById(boardId)
		return if (product != null) {
			TermConditionDetailResponse(
				boardId = product.boardId,
				productNameEn = product.productNameEn,
				productNameKh = product.productNameKh,
				productDescEn = product.productDescEn,
				productDescKh = product.productDescKh,
				productInterestRate = product.productInterestRate,
			)
		} else {
			TermConditionDetailResponse(boardId = 0)
		}
	}
}
