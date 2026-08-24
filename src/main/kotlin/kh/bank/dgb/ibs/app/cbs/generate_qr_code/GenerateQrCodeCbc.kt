package kh.bank.dgb.ibs.app.cbs.generate_qr_code

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class GenerateQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

data class GenerateQrCodeResponse(
	val qrCodeUrl: String? = null,
	val otpAuthString: String? = null,
)

/** Port of `USR2001_Adapter_GenerateQRCode` — calls CBS opcode `CIB11000221`. */
@RestController
@RequestMapping("/USR2001")
class GenerateQrCodeCbc(
	private val sbc: GenerateQrCodeSbc,
) {
	@PostMapping
	fun generate(@RequestBody request: RequestData<GenerateQrCodeRequest>): ResponseData<GenerateQrCodeResponse> =
		sbc.generate(request)
}
