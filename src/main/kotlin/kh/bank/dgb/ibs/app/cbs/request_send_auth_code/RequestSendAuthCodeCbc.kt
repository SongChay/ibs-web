package kh.bank.dgb.ibs.app.cbs.request_send_auth_code

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class RequestSendAuthCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerID: String? = null,
	val serviceID: String? = null,
	val phoneNumber: String? = null,
)

data class RequestSendAuthCodeResponse(
	val authTransactionID: BigDecimal? = null,
	val authTransactionDate: String? = null,
)

/**
 * Port of `USR2003_Adapter_RequestSendAuthCode` — calls CBS opcode `CIB11000212`.
 *
 * NOTE: the old adapter class was entirely commented out (dead code, never wired to an `@Adapter`
 * route) — this is a from-scratch port of that commented logic, not a currently-live endpoint.
 * Flagged in the batch port report.
 */
@RestController
@RequestMapping("/USR2003")
class RequestSendAuthCodeCbc(
	private val sbc: RequestSendAuthCodeSbc,
) {
	@PostMapping
	fun request(@RequestBody request: RequestData<RequestSendAuthCodeRequest>): ResponseData<RequestSendAuthCodeResponse> =
		sbc.request(request)
}
