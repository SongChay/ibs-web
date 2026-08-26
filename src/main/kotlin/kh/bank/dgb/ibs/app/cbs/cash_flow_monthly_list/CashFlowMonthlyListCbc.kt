package kh.bank.dgb.ibs.app.cbs.cash_flow_monthly_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowMonthlyListRequest(
	val userID: String? = null,
	val currencyCode: String? = null,
)

/** Port of the old `ABM1001_RES_InquiryCashFlowVo` grid row. */
data class CashFlowMonthlyListItem(
	val legend: String? = null,
	val income: java.math.BigDecimal? = null,
	val expense: java.math.BigDecimal? = null,
	val balance: java.math.BigDecimal? = null,
)

/** Shape the CBS response actually arrives in (`grid01`) — reshaped by the Sbc into four parallel
 *  lists before it goes back to the client, matching the old adapter's `Map<String, Object>`. */
data class CashFlowMonthlyListResponse(
	val legendList: List<String?>? = null,
	val incomeList: List<java.math.BigDecimal?>? = null,
	val expenseList: List<java.math.BigDecimal?>? = null,
	val balanceList: List<java.math.BigDecimal?>? = null,
)

/** Port of `ABM1001_Adapter_InquiryCashFlowMonthlyList` — calls CBS opcode `CIB11303411` (via the
 *  old `DGBEBankingService.processCIB11303411`).
 *
 *  NOT a plain pass-through: the old adapter unpacks the CBS `grid01` list into four parallel
 *  lists (`legendList`/`incomeList`/`expenseList`/`balanceList`) as a `Map<String, Object>` body.
 *  That reshaping is replicated in the Sbc. */
@RestController
@RequestMapping("/ABM1001")
class CashFlowMonthlyListCbc(
	private val cashFlowMonthlyListSbc: CashFlowMonthlyListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CashFlowMonthlyListRequest>): ResponseData<CashFlowMonthlyListResponse> {
		return cashFlowMonthlyListSbc.inquire(request)
	}
}
