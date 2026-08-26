package kh.bank.dgb.ibs.app.cbs.check_transfer_limit

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class CheckTransferLimitRequest(
	val userID: String? = null,
	val transactionCurrencyCode: String? = null,
	val transactionAmount: BigDecimal? = null,
	val eBankTransactionTypeCode: String? = null,
	val receiverBankCode: String? = null,
)

data class CheckTransferLimitResponse(
	val resultYn: String? = null,
)

/** Port of `TRS1008_Adapter_CheckTransferLimit` — calls CBS opcode `CIB11812312`
 *  (via the old `DGBEBankingService.processTRN0050`). Straight pass-through. */
@RestController
@RequestMapping("/TRS1008")
class CheckTransferLimitCbc(
	private val checkTransferLimitSbc: CheckTransferLimitSbc,
) {
	@PostMapping
	fun check(@RequestBody request: RequestData<CheckTransferLimitRequest>): ResponseData<CheckTransferLimitResponse> {
		return checkTransferLimitSbc.check(request)
	}
}
