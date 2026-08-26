package kh.bank.dgb.ibs.app.cbs.cash_flow_daily_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowDailyListRequest(
	val userID: String? = null,
	val cashFlowDate: String? = null,
)

/** Port of `ABM4001_RES_InquiryCashFlowListVo`. */
data class CashFlowDailyListItem(
	val seqNo: Long? = null,
	val userID: String? = null,
	val cashFlowDate: String? = null,
	val cashFlowTypeCode: String? = null,
	val cashFlowTypeCodeName: String? = null,
	val currencyCode: String? = null,
	val cashFlowAmount: java.math.BigDecimal? = null,
	val cashFlowTitle: String? = null,
	val cashFlowDescription: String? = null,
)

/** Port of `ABM4001_RES_WrapperInquiryCashFlowDailyListVo`. Same asymmetric `grid01`-in /
 *  `cashFlowList`-out wire shape as the yearly-list feature — see `CashFlowYearlyListResponse`. */
data class CashFlowDailyListResponse(
	val cashInTotalAmount: java.math.BigDecimal? = null,
	val cashOutTotalAmount: java.math.BigDecimal? = null,
	val totalAmount: java.math.BigDecimal? = null,
	@JsonAlias("grid01")
	val cashFlowList: List<CashFlowDailyListItem>? = null,
)

/** Port of `ABM4001_Adapter_InquiryCashFlowDailyList` — calls CBS opcode `CIB11003611` (via the
 *  old `DGBEBankingService.processCSH0005`). Plain pass-through. */
@RestController
@RequestMapping("/ABM4001")
class CashFlowDailyListCbc(
	private val cashFlowDailyListSbc: CashFlowDailyListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CashFlowDailyListRequest>): ResponseData<CashFlowDailyListResponse> {
		return cashFlowDailyListSbc.inquire(request)
	}
}
