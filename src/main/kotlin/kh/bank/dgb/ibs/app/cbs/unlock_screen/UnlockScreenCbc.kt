package kh.bank.dgb.ibs.app.cbs.unlock_screen

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UnlockScreenRequest(
	val userID: String? = null,
	val userPwd: String? = null,
)

data class UnlockScreenResponse(
	val resultYN: String? = null,
	val maxPasswordErrorCount: Int? = null,
	val passwordErrorCount: Int? = null,
)

/** Port of `INF2004_Adapter_UnlockedScreen` — calls CBS opcode `CIB11300291` (via the old
 *  `DGBEBankingService.processCIB11300191`). Non-pass-through: the old adapter sets
 *  `body.resultYN` from `header.result`, and on failure substitutes `${maxPasswordErrorCount}` /
 *  `${passwordErrorCount}` placeholders (old code used Commons-Lang `StrSubstitutor`) into
 *  `header.resultMessage` — replicated in `UnlockScreenSbc`. */
@RestController
@RequestMapping("/INF2004")
class UnlockScreenCbc(
	private val sbc: UnlockScreenSbc,
) {
	@PostMapping
	fun unlock(@RequestBody request: RequestData<UnlockScreenRequest>): ResponseData<UnlockScreenResponse> =
		sbc.unlock(request)
}
