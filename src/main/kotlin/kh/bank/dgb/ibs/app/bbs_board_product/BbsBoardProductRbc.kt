package kh.bank.dgb.ibs.app.bbs_board_product

import org.apache.ibatis.annotations.Mapper

/** Port of `BbsBoardProductDTO`. Same shape serves two different queries in the old mapper —
 *  `getTermConditionById` sources `productDescEn/Kh` from `bbs_board.board_html_en/kh` instead
 *  of `bbs_board_product.product_desc_en/kh` (kept as two resultMaps in the XML, one Kotlin type). */
data class BbsBoardProduct(
	val boardId: Int,
	val productNameEn: String? = null,
	val productNameKh: String? = null,
	val productDescEn: String? = null,
	val productDescKh: String? = null,
	val productInterestRate: String? = null,
)

/** Port of `BbsBoardProductDAO` — loan products, restricted to the 15 loan-category codes under
 *  `007006*` exactly as in the old app (see the mapper XML's `loanCategoryCodes` fragment). */
@Mapper
interface BbsBoardProductRbc {
	fun getLoanProducts(): List<BbsBoardProduct>
	fun getLoanProductById(id: Int): BbsBoardProduct?
	fun getLoanProductByProductCode(productCode: String): BbsBoardProduct?
	fun getTermConditionById(boardId: Int): BbsBoardProduct?
	fun getLoanProductNameKh(productCode: String): String?
}
