package kh.bank.dgb.ibs.app.cbs.transaction_limit_config

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class TransactionLimitConfigRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val eBankTransactionTypeCode: String? = null,
	val transactionCurrencyCode: String? = null,
)

data class TransactionLimitConfigResponse(
	val resultYN: String? = null,
	val maxAmount: BigDecimal? = null,
	val minAmount: BigDecimal? = null,
)

/** Port of `TRS2311_Adapter_InquiryTransactionLimitConfig` — calls CBS opcode `CIB11812311` (via
 *  the old `DGBEBankingService.processCIB11812311`). */
@RestController
@RequestMapping("/TRS2311")
class TransactionLimitConfigCbc(
	private val transactionLimitConfigSbc: TransactionLimitConfigSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<TransactionLimitConfigRequest>): ResponseData<TransactionLimitConfigResponse> {
		return transactionLimitConfigSbc.inquire(request)
	}
}
