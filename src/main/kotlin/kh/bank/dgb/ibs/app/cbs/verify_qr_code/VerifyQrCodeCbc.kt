package kh.bank.dgb.ibs.app.cbs.verify_qr_code

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

data class VerifyQrCodeResponse(
	val verifyYn: String? = null,
	val otpErrorCount: Int? = null,
)

/** Port of `USR2002_Adapter_VerifyQRCode` — calls CBS opcode `CIB11000211`. */
@RestController
@RequestMapping("/USR2002")
class VerifyQrCodeCbc(
	private val sbc: VerifyQrCodeSbc,
) {
	@PostMapping
	fun verify(@RequestBody request: RequestData<VerifyQrCodeRequest>): ResponseData<VerifyQrCodeResponse> =
		sbc.verify(request)
}
