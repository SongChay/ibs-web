package kh.bank.dgb.ibs.app.cbs.generate_qr_code

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class GenerateQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

data class GenerateQrCodeResponse(
	val qrCodeUrl: String? = null,
	val otpAuthString: String? = null,
)

/**
 * Port of two old endpoints that both zxing-encode the same CBS-derived OTP auth string, just
 * packaged differently (see `GenerateQrCodeSbc`'s doc comment):
 *  - `USR2001_Adapter_GenerateQRCode` (`/USR2001`) — normal `{header,body}` JSON adapter.
 *  - `QRCodeController.generateQrCode` (`GET /generateQrCode/{userID}.png`) — raw PNG image.
 *
 * Class-level `@RequestMapping` dropped since these two routes don't share a prefix.
 */
@RestController
class GenerateQrCodeCbc(
	private val sbc: GenerateQrCodeSbc,
) {
	@PostMapping("/USR2001")
	fun generate(@RequestBody request: RequestData<GenerateQrCodeRequest>): ResponseData<GenerateQrCodeResponse> =
		sbc.generate(request)

	@GetMapping("/generateQrCode/{userID}.png")
	fun generateImage(@PathVariable userID: String): ResponseEntity<ByteArray> =
		sbc.generateImage(userID)
}
