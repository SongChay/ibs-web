package kh.bank.dgb.ibs.configuration.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * Port of `CustomAuthenticationEntryPoint` — invoked when an unauthenticated request hits a
 * protected endpoint. HTTP 200 with an UNAUTHORIZED_REQUEST body, matching the old app exactly
 * (it never called response.setStatus either).
 */
@Component
class CustomAuthenticationEntryPoint(
	private val responseWriter: SecurityResponseWriter,
) : AuthenticationEntryPoint {

	override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
		responseWriter.write(response, ResponseResultUtils.makeResponse(false, ResponseResultCodeType.UNAUTHORIZED_REQUEST))
	}
}
