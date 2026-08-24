package kh.bank.dgb.ibs.app.cbs.cash_flow_create

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowCreateRequest(
	val userID: String? = null,
	val cashFlowDate: String? = null,
	val cashFlowTypeCode: String? = null,
	val cashFlowDescription: String? = null,
	val cashFlowAmount: java.math.BigDecimal? = null,
	val currencyCode: String? = null,
	val cashFlowTitle: String? = null,
)

/** Port of `ABM5001_RES_CreateCashFlowVo` — empty body in the old app. */
class CashFlowCreateResponse

/** Port of `ABM5001_Adapter_CreateCashFlow` — calls CBS opcode `CIB11003721` (via the old
 *  `DGBEBankingService.processCSH0003`). Plain pass-through.
 *
 *  NOTE: the old adapter class carries `@Deprecated` / "This Adapter doesn't used." — ported
 *  faithfully anyway per the batch instructions, but flag for removal consideration. */
@RestController
@RequestMapping("/ABM5001")
class CashFlowCreateCbc(
	private val sbc: CashFlowCreateSbc,
) {
	@PostMapping
	fun create(@RequestBody request: RequestData<CashFlowCreateRequest>): ResponseData<CashFlowCreateResponse> =
		sbc.create(request)
}
