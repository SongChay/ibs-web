package kh.bank.dgb.ibs.app.cbs.approval_by_final_approver

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.RequestUserHeaderVo
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.stereotype.Service

/** Port of the old `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING` constant. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

/** Port of `ApprovalStatusCodeType.APPROVED`. */
private const val APPROVAL_STATUS_CODE_APPROVED = "01"

/** Request/response shape for opcode `CIB11001022` (the "final approver action" opcode shared
 *  with `RejectByFinalApproverSbc`) — kept private here since only this Sbc's own orchestration
 *  calls it. */
private data class FinalApproverActionRequest(
	val userID: String? = null,
	val approvalStatusCode: String? = null,
	val approvalNo: Long = 0,
	val overrideScheduleTransferTime: String? = null,
)

private data class FinalApproverActionResponse(
	val resultYn: String? = null,
	val startTime: String? = null,
	val endTime: String? = null,
	@JsonProperty("grid01") val wingTransferResult: List<WingTransferResult>? = null,
	@JsonProperty("grid02") val accountTransferResult: List<AccountTransferResult>? = null,
)

/** Port of `USR0103_REQ_OTPCreateRequiredVo` / `USR0103_RES_OTPCreateRequiredVo` (opcode
 *  `CIB11000214`). Only used internally by this adapter's OTP-verification step. */
private data class OtpCreateRequiredRequest(
	val userID: String? = null,
)

private data class OtpCreateRequiredResponse(
	@param:JsonProperty("oTPCreateRequiredYN") val otpCreateRequiredYn: String? = null,
)

/** Port of `USR2002_REQ_VerifyQRCodeVo` / `USR2002_RES_VerifyQRCodeVo` (opcode `CIB11000211`). */
private data class VerifyQRCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int = 0,
	val firstYn: String? = null,
)

private data class VerifyQRCodeResponse(
	val verifyYn: String? = null,
	val otpErrorCount: Int = 0,
)

@Service
class ApprovalByFinalApproverSbc(
	private val connector: CoreBankingApiConnector,
	private val serviceStatusSbc: ServiceStatusSbc,
) {
	fun approve(request: RequestData<ApprovalByFinalApproverRequest>): ResponseData<ApprovalByFinalApproverResponse> {
		val header = request.header
		val body = request.body ?: ApprovalByFinalApproverRequest()

		// 1. Check allowed time window for final-approver execution.
		val status = serviceStatusSbc.getBlockingTime()
			?: return ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SERVICE_STATUS_NOT_FOUND),
				body = ApprovalByFinalApproverResponse(),
			)

		if (!status.allowed) {
			return ResponseData(
				header = ResponseResultUtils.makeResponse(
					false,
					ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.value,
					status.description ?: ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.description,
				),
				body = ApprovalByFinalApproverResponse(),
			)
		}

		// 2. Verify OTP.
		val otpVerification = verifyOtpCode(header, body)
		if (otpVerification.body?.verifyYn?.equals("Y", ignoreCase = true) != true) {
			return ResponseData(header = otpVerification.header, body = null)
		}

		// 3. Final-approver action, hardcoded to "approved" — note: userID here comes from the
		//    HEADER, unlike the OTP-verification step above which uses the BODY's userID; this
		//    matches the old adapter exactly.
		val approvalRequestBody = FinalApproverActionRequest(
			userID = header?.userID,
			approvalStatusCode = APPROVAL_STATUS_CODE_APPROVED,
			approvalNo = body.approvalNo,
			overrideScheduleTransferTime = body.overrideScheduleTransferTime,
		)
		val approvalResult = connector.post("CIB11001022", header?.languageCode, approvalRequestBody, FinalApproverActionResponse::class.java)

		return ResponseData(
			header = approvalResult.header,
			body = ApprovalByFinalApproverResponse(
				wingTransferResult = approvalResult.body?.wingTransferResult,
				accountTransferResult = approvalResult.body?.accountTransferResult,
			),
		)
	}

	/** Port of the old adapter's private `verifyOTPCode` method. */
	private fun verifyOtpCode(header: RequestUserHeaderVo?, body: ApprovalByFinalApproverRequest): ResponseData<VerifyQRCodeResponse> {
		val otpRequiredResult = connector.post(
			"CIB11000214",
			header?.languageCode,
			OtpCreateRequiredRequest(userID = body.userID),
			OtpCreateRequiredResponse::class.java,
		)

		val otpCreationRequired = otpRequiredResult.header?.result != true ||
			otpRequiredResult.body?.otpCreateRequiredYn?.equals("N", ignoreCase = true) != true

		if (otpCreationRequired) {
			return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.OTP_CREATE_REQUIRED))
		}

		return connector.post(
			"CIB11000211",
			header?.languageCode,
			VerifyQRCodeRequest(
				userID = body.userID,
				channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING,
				verifyCode = body.verifyCode,
				firstYn = "N",
			),
			VerifyQRCodeResponse::class.java,
		)
	}
}
