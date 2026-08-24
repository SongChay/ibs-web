package kh.bank.dgb.ibs.app.cbs.cash_flow_yearly_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowYearlyListRequest(
	val userID: String? = null,
	val year: String? = null,
)

/** Port of `ABM2001_RES_InquiryCashFlowListVo`. */
data class CashFlowYearlyListItem(
	val yearMonth: String? = null,
	val cashInAmount: java.math.BigDecimal? = null,
	val cashOutAmount: java.math.BigDecimal? = null,
)

/** Port of `ABM2001_RES_InquiryCashFlowVo`. The old Vo reads the CBS field as `grid01` but
 *  serializes it back out to the client as `cashFlowList` (asymmetric `@JsonGetter`/`@JsonSetter`)
 *  — `@JsonAlias` replicates that: `cashFlowList` is both the Kotlin property name and the
 *  outbound JSON key, while `grid01` is still accepted on the way in from CBS. */
data class CashFlowYearlyListResponse(
	val cashInTotalAmount: java.math.BigDecimal? = null,
	val cashOutTotalAmount: java.math.BigDecimal? = null,
	val totalAmount: java.math.BigDecimal? = null,
	@com.fasterxml.jackson.annotation.JsonAlias("grid01")
	val cashFlowList: List<CashFlowYearlyListItem>? = null,
)

/** Port of `ABM2001_Adapter_InquiryCashFlowYearlyList` — calls CBS opcode `CIB11003511` (via the
 *  old `DGBEBankingService.processCSH0002`). Plain pass-through. */
@RestController
@RequestMapping("/ABM2001")
class CashFlowYearlyListCbc(
	private val sbc: CashFlowYearlyListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CashFlowYearlyListRequest>): ResponseData<CashFlowYearlyListResponse> =
		sbc.inquire(request)
}
