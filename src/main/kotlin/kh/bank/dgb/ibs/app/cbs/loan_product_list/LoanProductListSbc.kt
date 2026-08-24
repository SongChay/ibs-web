package kh.bank.dgb.ibs.app.cbs.loan_product_list

import kh.bank.dgb.ibs.app.local.bbs_board_product.BbsBoardProductRbc
import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class LoanProductListSbc(
	private val connector: CoreBankingApiConnector,
	private val bbsBoardProductRbc: BbsBoardProductRbc,
) {
	fun inquire(request: RequestData<Unit>): ResponseData<LoanProductListResponse> {
		val result = connector.post("CIB11003811", request.header?.languageCode, request.body, LoanProductListResponse::class.java)

		val body = result.body ?: return result
		val enriched = body.loanProductList?.map { item ->
			item.copy(productNameKH = bbsBoardProductRbc.getLoanProductNameKh(item.productCode ?: ""))
		}

		return ResponseData(header = result.header, body = body.copy(loanProductList = enriched))
	}
}
