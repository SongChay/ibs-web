package kh.bank.dgb.ibs.app.cbs.register_frequently_used_account

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RegisterFrequentlyUsedAccountRequest(
	val userID: String? = null,
	val frequentAccountGroupNo: Long? = null,
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	val interBankCode: String? = null,
	val accountNo: String? = null,
	val receiverName: String? = null,
	val nickname: String? = null,
	val currencyCode: String? = null,
)

data class RegisterFrequentlyUsedAccountResponse(
	val resultYn: String? = null,
)

/** Port of `INF4102_Adapter_RegisterFrequentlyUsedAccount` — calls CBS opcode `CIB11002721`
 *  (via the old `DGBEBankingService.processTRN0013`). Non-pass-through: the old adapter derives
 *  `body.resultYn` ("Y"/"N") from `header.result` after the CBS call; replicated in
 *  `RegisterFrequentlyUsedAccountSbc`. */
@RestController
@RequestMapping("/INF4102")
class RegisterFrequentlyUsedAccountCbc(
	private val registerFrequentlyUsedAccountSbc: RegisterFrequentlyUsedAccountSbc,
) {
	@PostMapping
	fun register(
		@RequestBody request: RequestData<RegisterFrequentlyUsedAccountRequest>,
	): ResponseData<RegisterFrequentlyUsedAccountResponse> {
		return registerFrequentlyUsedAccountSbc.register(request)
	}
}
