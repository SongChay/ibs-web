package kh.bank.dgb.ibs.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.stereotype.Component

/**
 * Port of `CustomAuthenticationFailureHandler` — PARTIAL. The old handler special-cased
 * `CustomAuthenticationException` to surface core-banking-specific error data (password attempt
 * counts, substituted into the result message) — that branch is deferred along with the rest of
 * the login/core-banking integration (see `AuthenticationProviderImpl` in the old app).
 * `SessionAuthenticationException` (concurrent-login kick-off during the login POST itself) and
 * the generic fallback are pure security infra and ported now; anything else currently falls
 * through to UNKNOWN_ERROR until the deferred branch is built.
 */
@Component
class CustomAuthenticationFailureHandler(
	private val responseWriter: SecurityResponseWriter,
) : AuthenticationFailureHandler {

	override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
		val code = when (exception) {
			is SessionAuthenticationException -> ResponseResultCodeType.SESSION_MAX_COUNT
			else -> ResponseResultCodeType.UNKNOWN_ERROR
		}
		responseWriter.write(response, ResponseResultUtils.makeResponse(false, code))
	}
}
