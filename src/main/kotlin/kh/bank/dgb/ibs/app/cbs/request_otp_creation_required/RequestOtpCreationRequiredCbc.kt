package kh.bank.dgb.ibs.app.cbs.request_otp_creation_required

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RequestOtpCreationRequiredRequest(
	val userID: String? = null,
)

data class RequestOtpCreationRequiredResponse(
	val otpCreateRequiredYn: String? = null,
)

/** Port of `USR1003_Adapter_RequestOtpCreationRequired` — calls CBS opcode `CIB11000214`. */
@RestController
@RequestMapping("/USR1003")
class RequestOtpCreationRequiredCbc(
	private val sbc: RequestOtpCreationRequiredSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RequestOtpCreationRequiredRequest>): ResponseData<RequestOtpCreationRequiredResponse> =
		sbc.inquire(request)
}
