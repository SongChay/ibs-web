package kh.bank.dgb.ibs.app.cbs.cash_flow_update

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowUpdateRequest(
	val userID: String? = null,
	val seqNo: Long? = null,
	val cashFlowDate: String? = null,
	val cashFlowTypeCode: String? = null,
	val cashFlowDescription: String? = null,
	val cashFlowAmount: java.math.BigDecimal? = null,
	val currencyCode: String? = null,
	val cashFlowTitle: String? = null,
)

/** Port of `ABM4002_RES_UpdateCashFlowVo` — empty body in the old app. */
class CashFlowUpdateResponse

/** Port of `ABM4002_Adapter_UpdateCashFlow` — calls CBS opcode `CIB11003631` (via the old
 *  `DGBEBankingService.processCSH0004`). Plain pass-through.
 *
 *  NOTE: the old adapter class carries `@Deprecated` / "This Adapter doesn't used." — ported
 *  faithfully anyway per the batch instructions, but flag for removal consideration. */
@RestController
@RequestMapping("/ABM4002")
class CashFlowUpdateCbc(
	private val sbc: CashFlowUpdateSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<CashFlowUpdateRequest>): ResponseData<CashFlowUpdateResponse> =
		sbc.update(request)
}
