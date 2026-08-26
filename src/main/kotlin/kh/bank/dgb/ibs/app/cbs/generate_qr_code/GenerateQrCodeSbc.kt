package kh.bank.dgb.ibs.app.cbs.generate_qr_code

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * Port of `USR2001_Adapter_GenerateQRCode` and (its `generateQrCodeImage`) `QRCodeController` —
 * both call the same CBS opcode `CIB11000221` and zxing-encode the resulting `otpAuthString`, just
 * packaged differently. See `QrCodeEncoder`'s doc comment.
 *
 * Old adapter forced `channelTypeCode = "01"` (`BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING`)
 * on the request body before calling CBS, regardless of what the client sent — replicated below.
 */
@Service
class GenerateQrCodeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun generate(request: RequestData<GenerateQrCodeRequest>): ResponseData<GenerateQrCodeResponse> {
		val forcedBody = (request.body ?: GenerateQrCodeRequest()).copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)

		val cbsResult = coreBankingApiConnector.post(OPCODE, request.header?.languageCode, forcedBody, GenerateQrCodeResponse::class.java)

		if (cbsResult.header?.result != true || cbsResult.body == null) {
			return ResponseData(header = cbsResult.header, body = null)
		}

		val qrCodeUrl = cbsResult.body.otpAuthString?.let { QrCodeEncoder.encodePngBase64DataUrl(it, size = JSON_QR_SIZE) }
		return ResponseData(header = cbsResult.header, body = cbsResult.body.copy(qrCodeUrl = qrCodeUrl))
	}

	/** Port of `QRCodeController.generateQrCode` (`GET /generateQrCode/{userID}.png`) — same CBS
	 *  call as [generate], but returns the raw PNG instead of embedding it in JSON. Old code threw
	 *  on failure with no defined HTTP-status mapping; a plain 500 with an empty body stands in for
	 *  that here, since this is an image response and can't reuse the app's usual JSON error shape. */
	fun generateImage(userId: String): ResponseEntity<ByteArray> {
		val request = GenerateQrCodeRequest(userID = userId, channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		val cbsResult = coreBankingApiConnector.post(OPCODE, null, request, GenerateQrCodeResponse::class.java)
		val otpAuthString = cbsResult.body?.otpAuthString?.takeIf { cbsResult.header?.result == true }
			?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()

		val png = QrCodeEncoder.encodePng(otpAuthString, size = IMAGE_QR_SIZE)
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).contentLength(png.size.toLong()).body(png)
	}

	companion object {
		private const val OPCODE = "CIB11000221"
		private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"
		private const val JSON_QR_SIZE = 142
		private const val IMAGE_QR_SIZE = 200
	}
}
