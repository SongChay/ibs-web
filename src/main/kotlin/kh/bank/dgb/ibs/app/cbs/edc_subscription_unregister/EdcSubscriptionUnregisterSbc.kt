package kh.bank.dgb.ibs.app.cbs.edc_subscription_unregister

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Port of `TRS2541_Adapter_RegisterUnSubscriptionEDC#process`. This is NOT a single CBS
 * pass-through — same overall shape as [kh.bank.dgb.ibs.app.cbs.edc_subscription_register
 * .EdcSubscriptionRegisterSbc], with one important behavioral difference in the OTP step: when OTP
 * creation IS required (i.e. NOT `otpCreateRequiredYn == "N"`), the old `TRS2541` adapter does
 * NOT call SEC0004/CIB11000211 at all — it short-circuits straight to an `OTP_CREATE_REQUIRED`
 * error header. (`TRS2521`, by contrast, calls SEC0004 in both branches.) That distinction is
 * replicated exactly in [verifyOtpCode] below.
 *
 * As with `TRS2521`, the old adapter's fine-grained per-call swallow-and-log error handling is
 * simplified here to: any exception anywhere in the flow reports `UNKNOWN_ERROR` with an empty
 * body. Flagged for review.
 */
@Service
class EdcSubscriptionUnregisterSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	private val logger = LoggerFactory.getLogger(EdcSubscriptionUnregisterSbc::class.java)

	fun unregister(request: RequestData<EdcSubscriptionUnregisterRequest>): ResponseData<EdcSubscriptionUnregisterResponse> {
		return try {
			val verifyQrCodeVo = request.body?.verifyQRCodeVo
			if (verifyQrCodeVo != null) {
				val verifyResult = verifyOtpCode(request, verifyQrCodeVo)
				if (verifyResult.body?.verifyYn?.equals("Y", ignoreCase = true) == true) {
					registerUnSubscribe(request)
				} else {
					ResponseData(header = verifyResult.header, body = null)
				}
			} else {
				registerUnSubscribe(request)
			}
		} catch (e: Exception) {
			logger.error("Error execute transfer for final approver: ${e.message}")
			ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.UNKNOWN_ERROR),
				body = EdcSubscriptionUnregisterResponse(),
			)
		}
	}

	private fun registerUnSubscribe(request: RequestData<EdcSubscriptionUnregisterRequest>): ResponseData<EdcSubscriptionUnregisterResponse> {
		return try {
			coreBankingApiConnector.post("CIB11102541", request.header?.languageCode, request.body, EdcSubscriptionUnregisterResponse::class.java)
		} catch (e: Exception) {
			logger.error("Can not register transfer: ${e.message}")
			ResponseData()
		}
	}

	private fun verifyOtpCode(
		request: RequestData<EdcSubscriptionUnregisterRequest>,
		verifyQrCodeVo: VerifyQrCodeRequest,
	): ResponseData<VerifyQrCodeResponse> {
		return try {
			val otpResponse = coreBankingApiConnector.post(
				"CIB11000214",
				request.header?.languageCode,
				OtpCreateRequiredRequest(userID = verifyQrCodeVo.userID),
				OtpCreateRequiredResponse::class.java,
			)

			if (otpResponse.header?.result == true && otpResponse.body?.otpCreateRequiredYn?.equals("N", ignoreCase = true) == true) {
				coreBankingApiConnector.post(
					"CIB11000211",
					request.header?.languageCode,
					verifyQrCodeVo.copy(firstYn = "N"),
					VerifyQrCodeResponse::class.java,
				)
			} else {
				// Unlike TRS2521, TRS2541 does NOT call SEC0004 here — it short-circuits to an
				// OTP_CREATE_REQUIRED error, matching the old adapter exactly.
				ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.OTP_CREATE_REQUIRED))
			}
		} catch (e: Exception) {
			logger.error("Error verify OTP: ${e.message}")
			ResponseData()
		}
	}
}
