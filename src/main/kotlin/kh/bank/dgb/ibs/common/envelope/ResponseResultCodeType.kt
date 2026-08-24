package kh.bank.dgb.ibs.common.envelope

/**
 * Port of the old `ResponseResultCodeType` enum — the fixed result-code catalog existing clients
 * already key off of. Values/descriptions copied verbatim; do not renumber these, they're a wire
 * contract, not an internal implementation detail.
 */
enum class ResponseResultCodeType(val value: String, val description: String) {
	UNKNOWN_ERROR("CBK_ERR0000", "An unknown error occurred."),
	CBK_ERROR_CONNECT_EXCEPTION("CBK_ERR0001", "A unknown error responded from external server. (CBK_ERR0001)"),
	CBK_ERROR_SOCKET_TIMEOUT_EXCEPTION("CBK_ERR0002", "A unknown error responded from external server. (CBK_ERR0002)"),
	CBK_ERROR_CONNECTION_POOL_TIMEOUT_EXCEPTION("CBK_ERR0003", "A unknown error responded from external server. (CBK_ERR0003)"),
	CBK_NO_RESPONSE_HEADER_EB("CBK_ERR0004", "There is no response header from ebanking. (CBK_ERR0004)"),
	CBK_REQ_001("CBK_REQ_001", "Error occurred due to invalid request path. (CBK_REQ_001)"),

	INVALID_REQUEST("CBK_400", "Invalid request"),
	UNAUTHORIZED_REQUEST("CBK_401", "Unauthorized request"),
	REQUEST_NOT_FOUND("CBK_404", "Request not found"),

	SESSION_TIME_OUT("CBK_SES_001", "Session timeout."),
	SESSION_MAX_COUNT("CBK_SES_002", "You have been logout as there is login from the other device."),
	UNAUTHORIZED("CBK_SES_003", "Unauthorized access."),
	LOGIN_FAILED("CBK_LOG_001", "User ID or Password do not match.Please retry."),

	SUCCESS("CBK_0000", "Success.(CBK_0000)"),
	FAILED("CBK_0001", "Failed.(CBK_0001)"),
	ERROR_DOWNLOAD_FILE("CBK_FIL_001", "Can not download file.(CBK_FIL_001)"),
	FILE_NOT_FOUND("CBK_FIL_002", "Sorry! We don't have requested file.(CBK_FIL_002)"),

	SERVICE_STATUS_OFF("CBK_SEV_001", "smartBiz is currently unavailable. Sorry for your inconvenience.(CBK_SEV_001)"),
	SERVICE_STATUS_TIME_OFF("CBK_SEV_002", "Our operation hours for transfer execution is from 8am - 6pm Cambodia (time zone).(CBK_SEV_002)"),
	SERVICE_STATUS_NOT_FOUND("CBK_SEV_003", "Service status not found.(CBK_SEV_003)"),

	OTP_ERROR_INVALID("CBK_OTP_001", "The OTP authentication number does not match. Please try again.(CBK_OTP_001)"),
	OTP_ERROR_MAX_COUNT("CBK_OTP_002", "You made OTP verification error more than 5 times. Please visit the nearest branch of DGBank.(CBK_OTP_002)"),
	OTP_CREATE_REQUIRED("CBK_OTP_003", "Please do verify OTP.(CBK_OTP_003)"),
	TRANSFER_MAX_LIMIT("CBK_TRS_001", "You have exceeded the the maximum transfer.(CBK_TRS_001)"),
	;
}
