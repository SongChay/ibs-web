package kh.bank.dgb.ibs.app.cbs.execute_transfer_final_approver

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.stereotype.Service

/** Port of `USR0103_REQ/RES_OTPCreateRequiredVo` — internal to this feature's OTP-verification
 *  step, never returned to our own client directly. */
private data class OtpCreateRequiredRequest(val userID: String? = null)

private data class OtpCreateRequiredResponse(
	@param:JsonProperty("oTPCreateRequiredYN")
	val otpCreateRequiredYn: String? = null,
)

/** Port of `USR2002_RES_VerifyQRCodeVo`. */
private data class VerifyQrCodeResponse(
	val verifyYn: String? = null,
	val otpErrorCount: Int? = null,
)

/** Port of `TRS1102_RES_TransferVo`, private to this feature — see `TransferResponse` in the
 *  sibling `transfer` feature for the identical shape used by the plain (non-final-approver)
 *  register-transfer call. */
private data class TransferCallResponse(
	val approvalNo: Long? = null,
	val resultYn: String? = null,
	@JsonProperty("grid02")
	val accountTransferResult: List<AccountTransferResultItem>? = null,
)

@Service
class ExecuteTransferFinalApproverSbc(
	private val connector: CoreBankingApiConnector,
	private val serviceStatusSbc: ServiceStatusSbc,
) {

	/** Port of `TRS1104_Adapter_ExecuteTransferForFinalApper.process`. */
	fun execute(request: RequestData<ExecuteTransferFinalApproverRequest>): ResponseData<ExecuteTransferFinalApproverResponse> {
		val languageCode = request.header?.languageCode

		// 1. Check service status of Corporate Banking to block system.
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

		// 2. Verify OTP.
		val otpResult = verifyOtp(request.body?.verifyQRCodeVo, languageCode)
		if (otpResult.body?.verifyYn.equals("Y", ignoreCase = true) != true) {
			return ResponseData(header = otpResult.header, body = ExecuteTransferFinalApproverResponse())
		}

		// 3. Register transfer.
		val transferResult = registerTransfer(request.body, languageCode)
		return ResponseData(
			header = transferResult.header,
			body = ExecuteTransferFinalApproverResponse(accountTransferResult = transferResult.body?.accountTransferResult),
		)
	}

	/** Port of `verifyOTPCode`: check whether OTP creation/verification is required, then verify it. */
	private fun verifyOtp(verifyQrCode: VerifyQrCodeRequest?, languageCode: String?): ResponseData<VerifyQrCodeResponse> {
		val otpCheck = connector.post(
			"CIB11000214",
			languageCode,
			OtpCreateRequiredRequest(userID = verifyQrCode?.userID),
			OtpCreateRequiredResponse::class.java,
		)

		if (otpCheck.header?.result == true && otpCheck.body?.otpCreateRequiredYn.equals("N", ignoreCase = true)) {
			val verifyRequest = verifyQrCode?.copy(firstYn = "N")
			return connector.post("CIB11000211", languageCode, verifyRequest, VerifyQrCodeResponse::class.java)
		}

		return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.OTP_CREATE_REQUIRED))
	}

	/** Port of `registerTransfer`: enforce the max-transfer-count limit, clear the schedule for
	 *  immediate transfers (same rule as the plain `transfer` feature), then call CBS. */
	private fun registerTransfer(
		body: ExecuteTransferFinalApproverRequest?,
		languageCode: String?,
	): ResponseData<TransferCallResponse> {
		val transferList = body?.transferList
		if (transferList != null && transferList.size > TRANSFER_MAX_LIMIT) {
			return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.TRANSFER_MAX_LIMIT))
		}

		val adjustedBody = if (body != null && body.transferTypeCode.equals("0001", ignoreCase = true)) {
			body.copy(scheduleDate = null, scheduleTime = null)
		} else {
			body
		}

		return connector.post("CIB11001021", languageCode, adjustedBody, TransferCallResponse::class.java)
	}

	companion object {
		private const val TRANSFER_MAX_LIMIT = 1000
	}
}
