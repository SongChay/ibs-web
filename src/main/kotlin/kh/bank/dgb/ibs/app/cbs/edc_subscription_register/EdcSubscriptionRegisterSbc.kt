package kh.bank.dgb.ibs.app.cbs.edc_subscription_register

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Port of `TRS2521_Adapter_RegisterSubscriptionEDC#process`. This is NOT a single CBS pass-through:
 *
 *  - When the request carries a `verifyQRCodeVo`, first checks whether OTP creation is required
 *    (CBS opcode `CIB11000214`), then verifies the QR/OTP code (CBS opcode `CIB11000211`,
 *    forcing `firstYn = "N"` when OTP creation was NOT required). Only if that verification comes
 *    back `verifyYn == "Y"` does it proceed to actually register the subscription (opcode
 *    `CIB11102521`); otherwise it returns the verification call's header with an empty body.
 *  - When there's no `verifyQRCodeVo` (e.g. a sub-user request), it registers directly.
 *
 * The old adapter wraps each CBS call in its own try/catch that just logs and returns an empty
 * envelope on failure (silently swallowing errors), with an outer catch that maps `BaseException`
 * to `UNKNOWN_ERROR` and otherwise only logs. That fine-grained swallow-and-continue behavior is
 * simplified here to: any exception anywhere in the flow reports `UNKNOWN_ERROR` with an empty
 * body. Flagged for review — this is a deliberate simplification of unusual legacy error handling,
 * not a byte-for-byte port of it.
 */
@Service
class EdcSubscriptionRegisterSbc(
	private val connector: CoreBankingApiConnector,
) {
	private val logger = LoggerFactory.getLogger(EdcSubscriptionRegisterSbc::class.java)

	fun register(request: RequestData<EdcSubscriptionRegisterRequest>): ResponseData<EdcSubscriptionRegisterResponse> =
		try {
			val verifyQrCodeVo = request.body?.verifyQRCodeVo
			if (verifyQrCodeVo != null) {
				val verifyResult = verifyOtpCode(request, verifyQrCodeVo)
				if (verifyResult.body?.verifyYn?.equals("Y", ignoreCase = true) == true) {
					registerSubscription(request)
				} else {
					ResponseData(header = verifyResult.header, body = null)
				}
			} else {
				registerSubscription(request)
			}
		} catch (e: Exception) {
			logger.error("Error execute transfer for final approver: ${e.message}")
			ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.UNKNOWN_ERROR),
				body = EdcSubscriptionRegisterResponse(),
			)
		}

	private fun registerSubscription(request: RequestData<EdcSubscriptionRegisterRequest>): ResponseData<EdcSubscriptionRegisterResponse> =
		try {
			connector.post("CIB11102521", request.header?.languageCode, request.body, EdcSubscriptionRegisterResponse::class.java)
		} catch (e: Exception) {
			logger.error("Can not register transfer: ${e.message}")
			ResponseData()
		}

	private fun verifyOtpCode(
		request: RequestData<EdcSubscriptionRegisterRequest>,
		verifyQrCodeVo: VerifyQrCodeRequest,
	): ResponseData<VerifyQrCodeResponse> =
		try {
			val otpResponse = connector.post(
				"CIB11000214",
				request.header?.languageCode,
				OtpCreateRequiredRequest(userID = verifyQrCodeVo.userID),
				OtpCreateRequiredResponse::class.java,
			)

			// Both branches call SEC0004/CIB11000211 — the only difference is whether `firstYn`
			// gets forced to "N" first, matching the old adapter's `verifyOTPCode` exactly.
			val effectiveVerifyQrCodeVo = if (otpResponse.header?.result == true &&
				otpResponse.body?.otpCreateRequiredYn?.equals("N", ignoreCase = true) == true
			) {
				verifyQrCodeVo.copy(firstYn = "N")
			} else {
				verifyQrCodeVo
			}
			connector.post("CIB11000211", request.header?.languageCode, effectiveVerifyQrCodeVo, VerifyQrCodeResponse::class.java)
		} catch (e: Exception) {
			logger.error("Error verify OTP: ${e.message}")
			ResponseData()
		}
}
