package kh.bank.dgb.ibs.common.exception

import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import org.springframework.http.HttpStatus

/**
 * Unchecked replacement for the old `BaseException` hierarchy (bizmob.corpbanking.common.exception) —
 * Kotlin has no checked exceptions, and every old adapter's `process()` declaring `throws Exception`
 * was pure boilerplate anyway. Carries the result code the old code looked up by hand in each catch
 * block; GlobalExceptionHandler turns this into the legacy `{"header": {resultCode, resultMessage}}`
 * response shape.
 */
open class IbsException(
	val resultCode: ResponseResultCodeType,
	val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
	message: String? = resultCode.description,
	cause: Throwable? = null,
) : RuntimeException(message, cause)

/** Port of `SessionTimeoutException` — the encrypted-channel session has gone idle past the
 *  configured timeout (see EncryptedEnvelopeFilter). */
class SessionTimeoutException(message: String = "Session timeout") :
	IbsException(ResponseResultCodeType.SESSION_TIME_OUT, HttpStatus.UNAUTHORIZED, message)

/** Port of `CustomAuthenticationException` / the old adapter's ad-hoc "unauthorized" throws. */
class UnauthorizedException(message: String = "Unauthorized request") :
	IbsException(ResponseResultCodeType.UNAUTHORIZED_REQUEST, HttpStatus.UNAUTHORIZED, message)
