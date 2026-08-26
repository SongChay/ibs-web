package kh.bank.dgb.ibs.app.cbs.wing_transfer_final_approval

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
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

/** Port of `TRS5101_RES_RegisterWingTransferVo` — the CBS-facing shape (`wingTransferResult` <->
 *  `grid01`, same key both directions), as opposed to `WingTransferFinalApprovalResponse` which
 *  is this feature's own hand-built outward response. */
private data class WingTransferRegistrationResult(
	val approvalNo: Long? = null,
	@JsonProperty("grid01")
	val wingTransferResult: List<WingTransferResultItem>? = null,
)

/**
 * Port of `TRS5102_Adapter_ExecuteWingTransferForFinalApprover.process(...)` plus its two private
 * helpers `verifyOTPCode` and `registerWingTransfer`. See the doc comment on
 * `WingTransferFinalApprovalCbc` for the overall shape; this is the real implementation.
 *
 * As in the TRS4102 port, the old checked-exception-as-control-flow (`ServiceStatusNotFoundException`,
 * `ServiceStatusBlockingTimeExeption`, plus a generic `UNKNOWN_ERROR` catch-all branch that TRS4102
 * didn't have) is replaced with plain early-return branching — behaviorally identical.
 */
@Service
class WingTransferFinalApprovalSbc(
	private val serviceStatusSbc: ServiceStatusSbc,
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	private val logger = LoggerFactory.getLogger(WingTransferFinalApprovalSbc::class.java)

	fun execute(request: RequestData<WingTransferFinalApprovalRequest>): ResponseData<WingTransferFinalApprovalResponse> {
		val status = serviceStatusSbc.getBlockingTime()

		if (status == null) {
			return ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SERVICE_STATUS_NOT_FOUND),
				body = WingTransferFinalApprovalResponse(),
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
				body = WingTransferFinalApprovalResponse(),
			)
		}

		val otpResult = verifyOtp(request)
		if (!otpResult.body?.verifyYn.equals("Y", ignoreCase = true)) {
			return ResponseData(header = otpResult.header, body = WingTransferFinalApprovalResponse())
		}

		val transferResult = registerWingTransfer(request)
		return ResponseData(
			header = transferResult.header,
			body = WingTransferFinalApprovalResponse(wingTransferResult = transferResult.body?.wingTransferResult),
		)
	}

	private fun verifyOtp(request: RequestData<WingTransferFinalApprovalRequest>): ResponseData<VerifyQrCodeResponse> {
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

	private fun registerWingTransfer(request: RequestData<WingTransferFinalApprovalRequest>): ResponseData<WingTransferRegistrationResult> {
		val body = request.body ?: WingTransferFinalApprovalRequest()
		// Port of: `item.setReceiverCountryCode("KHM")` for every item in the transfer list.
		val withKhmCountryCode = body.copy(transferList = body.transferList?.map { it.copy(receiverCountryCode = "KHM") })
		return coreBankingApiConnector.post(OPCODE_REGISTER_TRANSFER, request.header?.languageCode, withKhmCountryCode, WingTransferRegistrationResult::class.java)
	}

	companion object {
		private const val OPCODE_OTP_CREATE_REQUIRED = "CIB11000214"
		private const val OPCODE_VERIFY_QR_CODE = "CIB11000211"
		private const val OPCODE_REGISTER_TRANSFER = "CIB11001921"
	}
}
