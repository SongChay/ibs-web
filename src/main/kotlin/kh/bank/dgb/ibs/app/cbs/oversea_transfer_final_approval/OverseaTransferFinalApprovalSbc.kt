package kh.bank.dgb.ibs.app.cbs.oversea_transfer_final_approval

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/** Port of `USR0103_REQ/RES_OTPCreateRequiredVo` — internal to this feature's OTP-verification
 *  step, never returned to our own client directly. */
private data class OtpCreateRequiredRequest(val userID: String? = null)

private data class OtpCreateRequiredResponse(
	@param:JsonProperty("oTPCreateRequiredYN")
	val otpCreateRequiredYn: String? = null,
)

private data class VerifyQrCodeResponse(val verifyYn: String? = null, val otpErrorCount: Int? = null)

private data class TransferRegistrationResult(val approvalNo: Long? = null)

/**
 * Port of `TRS4102_Adapter_RegisterOverseaTransferByFinalApprover.process(...)` plus its two
 * private helpers `verifyOTPCode` and `registerTransfer`. See the doc comment on
 * `OverseaTransferFinalApprovalCbc` for the overall shape; this is the real implementation.
 *
 * The old code used checked exceptions (`ServiceStatusNotFoundException`,
 * `ServiceStatusBlockingTimeExeption`) purely as control flow to pick which header to return —
 * there was no actual exceptional condition being recovered from. Replicated here as plain
 * early-return branching instead, which is behaviorally identical.
 */
@Service
class OverseaTransferFinalApprovalSbc(
	private val serviceStatusSbc: ServiceStatusSbc,
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	private val logger = LoggerFactory.getLogger(OverseaTransferFinalApprovalSbc::class.java)

	fun execute(request: RequestData<OverseaTransferFinalApprovalRequest>): ResponseData<OverseaTransferFinalApprovalResponse> {
		val status = serviceStatusSbc.getBlockingTime()

		if (status == null) {
			return ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SERVICE_STATUS_NOT_FOUND),
				body = OverseaTransferFinalApprovalResponse(),
			)
		}

		if (!status.allowed) {
			logger.info(">>> Service Status Info: {}", status)
			return ResponseData(
				header = ResponseResultUtils.makeResponse(
					false,
					ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.value,
					status.description ?: ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.description,
				),
				body = OverseaTransferFinalApprovalResponse(),
			)
		}

		val otpResult = verifyOtp(request)
		if (!otpResult.body?.verifyYn.equals("Y", ignoreCase = true)) {
			return ResponseData(header = otpResult.header, body = OverseaTransferFinalApprovalResponse())
		}

		val transferResult = registerTransfer(request)
		return ResponseData(header = transferResult.header, body = OverseaTransferFinalApprovalResponse())
	}

	private fun verifyOtp(request: RequestData<OverseaTransferFinalApprovalRequest>): ResponseData<VerifyQrCodeResponse> {
		val verifyQrCode = request.body?.verifyQRCodeVo
		val otpCreateRequiredResult = coreBankingApiConnector.post(
			OPCODE_OTP_CREATE_REQUIRED,
			request.header?.languageCode,
			OtpCreateRequiredRequest(userID = verifyQrCode?.userID),
			OtpCreateRequiredResponse::class.java,
		)

		val otpCreateRequired = otpCreateRequiredResult.body?.otpCreateRequiredYn
		return if (otpCreateRequiredResult.header?.result == true && otpCreateRequired.equals("N", ignoreCase = true)) {
			logger.info(">> Start verify otp : firstYn = N ")
			val verifyRequestBody = (verifyQrCode ?: VerifyQrCodeRequest()).copy(firstYn = "N")
			coreBankingApiConnector.post(OPCODE_VERIFY_QR_CODE, request.header?.languageCode, verifyRequestBody, VerifyQrCodeResponse::class.java)
		} else {
			logger.info(">> Need to do verify OTP. ")
			ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.OTP_CREATE_REQUIRED))
		}
	}

	private fun registerTransfer(request: RequestData<OverseaTransferFinalApprovalRequest>): ResponseData<TransferRegistrationResult> {
		return coreBankingApiConnector.post(OPCODE_REGISTER_TRANSFER, request.header?.languageCode, request.body, TransferRegistrationResult::class.java)
	}

	companion object {
		private const val OPCODE_OTP_CREATE_REQUIRED = "CIB11000214"
		private const val OPCODE_VERIFY_QR_CODE = "CIB11000211"
		private const val OPCODE_REGISTER_TRANSFER = "CIB11301721"
	}
}
