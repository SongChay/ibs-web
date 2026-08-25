package kh.bank.dgb.ibs.configuration.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo
import kh.bank.dgb.ibs.configuration.security.CoreBankingAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.stereotype.Component

/**
 * Port of `CustomAuthenticationFailureHandler`. Now complete: `CoreBankingAuthenticationException`
 * (the restored `CustomAuthenticationException` equivalent) surfaces CBS's own result code/message
 * straight through — already message-substituted with password-attempt counts by
 * `DefaultCoreBankingAuthClient`, matching the old app's `AuthenticationProviderImpl`.
 * `SessionAuthenticationException` (concurrent-login kick-off during the login POST itself) and
 * the generic fallback are unchanged.
 */
@Component
class CustomAuthenticationFailureHandler(
	private val responseWriter: SecurityResponseWriter,
) : AuthenticationFailureHandler {

	override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
		val header = when (exception) {
			is CoreBankingAuthenticationException -> ResponseUserHeaderVo(
				result = false,
				resultCode = exception.resultCode ?: ResponseResultCodeType.UNKNOWN_ERROR.value,
				resultMessage = exception.resultMessage ?: ResponseResultCodeType.UNKNOWN_ERROR.description,
			)
			is SessionAuthenticationException -> ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SESSION_MAX_COUNT)
			else -> ResponseResultUtils.makeResponse(false, ResponseResultCodeType.UNKNOWN_ERROR)
		}
		responseWriter.write(response, header)
	}
}
