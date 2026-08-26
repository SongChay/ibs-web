package kh.bank.dgb.ibs.app.cbs.payroll_payment_register

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.stereotype.Service

/** Intermediate request/response for the "OTP create required" check (old
 *  `USR0103_REQ/RES_OTPCreateRequiredVo`, opcode `CIB11000214`). Not exposed as its own feature —
 *  only used internally by the OTP-gated register flow below. */
private data class OtpCreateRequiredRequest(
	val userID: String? = null,
)

/** Old Vo has a genuinely asymmetric (and inconsistently-cased) Jackson mapping:
 *  `@JsonGetter("oTPCreateRequiredYn")` / `@JsonSetter("oTPCreateRequiredYN")` — the setter key
 *  (used to deserialize CBS's response) differs in case from the getter key. Replicated exactly. */
private data class OtpCreateRequiredResponse(
	@param:JsonProperty("oTPCreateRequiredYN")
	@get:JsonProperty("oTPCreateRequiredYn")
	val otpCreateRequiredYn: String? = null,
)

/** Old `USR2002_RES_VerifyQRCodeVo`, opcode `CIB11000211`. */
private data class VerifyQrCodeResponse(
	val verifyYn: String? = null,
	val otpErrorCount: Int? = null,
)

/**
 * Port of the active (non-commented-out) `PYR1103_Adapter_RegisterPayrollPayment.process`. This
 * is genuinely more than a pass-through:
 *
 *  1. Look up the service-status blocking-time record (old `ServiceStatusService.getBlockingTime()`)
 *     via the shared `ServiceStatusSbc.getBlockingTime()` — this same check is also needed by
 *     `approval_by_final_approver`, `oversea_transfer_final_approval`, `wing_transfer_final_approval`,
 *     and `execute_transfer_final_approver`, so it lives once in `app/local/service_status/` rather
 *     than being duplicated per feature.
 *  2. If no status record exists, fail with `SERVICE_STATUS_NOT_FOUND`.
 *  3. If approval isn't currently allowed, fail with `SERVICE_STATUS_TIME_OFF`.
 *  4. Otherwise verify OTP: call `CIB11000214` (OTP-create-required check); if it says
 *     `otpCreateRequiredYn == "N"`, call `CIB11000211` (verify QR/OTP code) with `firstYn = "N"`.
 *     If OTP creation is still required, fail with `OTP_CREATE_REQUIRED`.
 *  5. If OTP verification succeeded (`verifyYn == "Y"`), register the transfer via `CIB11300221`
 *     and set `resultYn` from the transfer response header's `result` flag.
 *
 * FLAGGED BUG FIX vs. the old code: in the old adapter, after the final "register transfer" step,
 * `resData.setHeader(resDataTransfer.getHeader())` is called but `resData.setBody(...)` never is
 * — `resData` was a bare `new ResponseData<>()` with a null body, so
 * `resData.getBody().setResultYn(...)` would NPE (silently swallowed by the adapter's own generic
 * `catch (Exception e)`, which just logs and returns whatever `resData` was at that point — header
 * set, body null). This port instead carries `transferResult.body` through and sets `resultYn` on
 * it, which is almost certainly the intended behavior. Flagged for extra scrutiny.
 */
@Service
class PayrollPaymentRegisterSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
	private val serviceStatusSbc: ServiceStatusSbc,
) {
	// Old code wrapped this whole flow in a generic `catch (Exception e)` that just logged and fell
	// through to whatever `resData` was — that's what `GlobalExceptionHandler`'s catch-all
	// (Exception -> UNKNOWN_ERROR, HTTP 200) already does uniformly for every endpoint, so it's not
	// duplicated here, matching the precedent set by the sibling `execute_transfer_final_approver`.
	fun register(request: RequestData<PayrollPaymentRegisterRequest>): ResponseData<PayrollPaymentRegisterResponse> {
		val languageCode = request.header?.languageCode

		// 1. Check service status of Corporate Banking to block system
		val status = serviceStatusSbc.getBlockingTime()
			?: return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SERVICE_STATUS_NOT_FOUND))

		if (!status.allowed) {
			return ResponseData(
				header = ResponseResultUtils.makeResponse(
					false,
					ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.value,
					status.description ?: ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.description,
				),
			)
		}

		// 2. Verify OTP
		val verifyOtpResult = verifyOtpCode(request, languageCode)
		val verifyBody = verifyOtpResult.body
		if (verifyBody == null || !verifyBody.verifyYn.equals("Y", ignoreCase = true)) {
			return ResponseData(header = verifyOtpResult.header)
		}

		// 3. Register Transfer
		val transferResult = coreBankingApiConnector.post(OPCODE_REGISTER, languageCode, request.body, PayrollPaymentRegisterResponse::class.java)
		val resultYn = if (transferResult.header?.result == true) "Y" else "N"
		return ResponseData(
			header = transferResult.header,
			body = (transferResult.body ?: PayrollPaymentRegisterResponse()).copy(resultYn = resultYn),
		)
	}

	private fun verifyOtpCode(
		request: RequestData<PayrollPaymentRegisterRequest>,
		languageCode: String?,
	): ResponseData<VerifyQrCodeResponse> {
		val verifyQrCodeVo = request.body?.verifyQRCodeVo

		val otpRequiredResult = coreBankingApiConnector.post(
			OPCODE_OTP_CREATE_REQUIRED,
			languageCode,
			OtpCreateRequiredRequest(userID = verifyQrCodeVo?.userID),
			OtpCreateRequiredResponse::class.java,
		)

		val otpCreateRequired = otpRequiredResult.body?.otpCreateRequiredYn
		if (otpRequiredResult.header?.result != true || !otpCreateRequired.equals("N", ignoreCase = true)) {
			return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.OTP_CREATE_REQUIRED))
		}

		val verifyRequestBody = verifyQrCodeVo?.copy(firstYn = "N")
		return coreBankingApiConnector.post(OPCODE_VERIFY_QR_CODE, languageCode, verifyRequestBody, VerifyQrCodeResponse::class.java)
	}

	companion object {
		private const val OPCODE_OTP_CREATE_REQUIRED = "CIB11000214"
		private const val OPCODE_VERIFY_QR_CODE = "CIB11000211"
		private const val OPCODE_REGISTER = "CIB11300221"
	}
}
