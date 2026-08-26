package kh.bank.dgb.ibs.app.cbs.cash_flow_monthly_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `ABM1001_RES_InquiryCashFlowMonthlyVo` — the raw CBS response shape (`grid01`), kept
 *  private to this Sbc since only the reshaped `CashFlowMonthlyListResponse` crosses the HTTP
 *  boundary. */
private data class CashFlowMonthlyCbsResponse(
	val grid01: List<CashFlowMonthlyListItem>? = null,
)

@Service
class CashFlowMonthlyListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CashFlowMonthlyListRequest>): ResponseData<CashFlowMonthlyListResponse> {
		val cbsResult = coreBankingApiConnector.post("CIB11303411", request.header?.languageCode, request.body, CashFlowMonthlyCbsResponse::class.java)
		val items = cbsResult.body?.grid01.orEmpty()

		val body = CashFlowMonthlyListResponse(
			legendList = items.map { it.legend },
			incomeList = items.map { it.income },
			expenseList = items.map { it.expense },
			balanceList = items.map { it.balance },
		)

		return ResponseData(header = cbsResult.header, body = body)
	}
}
