package kh.bank.dgb.ibs.app.cbs.generate_qr_code

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of `USR2001_Adapter_GenerateQRCode`.
 *
 * Old adapter forced `channelTypeCode = "01"` (`BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING`)
 * on the request body before calling CBS, regardless of what the client sent — replicated below.
 * (It also set the same value on the request *header*, but the new `CoreBankingApiConnector.post`
 * has no per-request channel-code parameter — that header mutation has no analog here and is not
 * replicated; see the batch port report.)
 *
 * TODO: the old adapter renders `otpAuthString` (an otpauth:// TOTP URI) into a QR PNG using
 * `com.google.zxing` and returns it as a `data:image/png;base64,...` URL in `qrCodeUrl`. That
 * library isn't a project dependency yet, and this port isn't allowed to touch `build.gradle.kts`
 * — so `qrCodeUrl` is left null here rather than guessing at a substitute encoder. A real QR
 * library (`com.google.zxing:core` + `:javase`, as in the old app) needs to be added and this
 * method updated before this endpoint can be used for real OTP enrollment. Old app threw
 * `Exception("Can not generateQrCode")` when `resData.header.result` was false; replicated as
 * returning the CBS failure response as-is (no body) rather than throwing, since throwing from a
 * service method has no old-adapter-equivalent exception-to-HTTP-status mapping to rely on here.
 */
@Service
class GenerateQrCodeSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun generate(request: RequestData<GenerateQrCodeRequest>): ResponseData<GenerateQrCodeResponse> {
		val forcedBody = (request.body ?: GenerateQrCodeRequest()).copy(channelTypeCode = "01")

		val cbsResult = connector.post(
			"CIB11000221",
			request.header?.languageCode,
			forcedBody,
			GenerateQrCodeResponse::class.java,
		)

		if (cbsResult.header?.result != true || cbsResult.body == null) {
			return ResponseData(header = cbsResult.header, body = null)
		}

		// TODO: replace with a real QR PNG data URL (com.google.zxing encode -> PNG -> base64,
		// exactly as the old adapter did) once that dependency is added. Left null for now.
		return ResponseData(
			header = cbsResult.header,
			body = cbsResult.body.copy(qrCodeUrl = null),
		)
	}
}
