package kh.bank.dgb.ibs.app.cbs.verify_authentication_code

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class VerifyAuthenticationCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val serviceID: String? = null,
	val authenticationCode: String? = null,
	val authTransactionID: BigDecimal? = null,
	val authTransactionDate: String? = null,
)

/** Port of `USR2004_RES_VerifyAuthenticationCodeVo` — genuinely empty in the old app (no fields,
 *  just a marker success/failure carried entirely by the response header). */
class VerifyAuthenticationCodeResponse

/**
 * Port of `USR2004_Adapter_VerifyAuthenticationCode` — calls CBS opcode `CIB11000213`.
 *
 * NOTE: the old adapter class was entirely commented out (dead code, never wired to an `@Adapter`
 * route) — this is a from-scratch port of that commented logic, not a currently-live endpoint.
 * Flagged in the batch port report.
 */
@RestController
@RequestMapping("/USR2004")
class VerifyAuthenticationCodeCbc(
	private val verifyAuthenticationCodeSbc: VerifyAuthenticationCodeSbc,
) {
	@PostMapping
	fun verify(@RequestBody request: RequestData<VerifyAuthenticationCodeRequest>): ResponseData<VerifyAuthenticationCodeResponse> {
		return verifyAuthenticationCodeSbc.verify(request)
	}
}
