package kh.bank.dgb.ibs.app.cbs.cash_flow_delete

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CashFlowDeleteRequest(
	val userID: String? = null,
	val seqNo: Long? = null,
)

/** Port of `ABM4003_RES_DeleteCashFlowVo` — empty body in the old app. */
class CashFlowDeleteResponse

/** Port of `ABM4003_Adapter_DeleteCashFlow` — calls CBS opcode `CIB11003632` (via the old
 *  `DGBEBankingService.processCSH0006`). Plain pass-through.
 *
 *  NOTE: the old adapter class carries `@Deprecated` / "This Adapter doesn't used." — ported
 *  faithfully anyway per the batch instructions, but flag for removal consideration. */
@RestController
@RequestMapping("/ABM4003")
class CashFlowDeleteCbc(
	private val sbc: CashFlowDeleteSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<CashFlowDeleteRequest>): ResponseData<CashFlowDeleteResponse> =
		sbc.delete(request)
}
