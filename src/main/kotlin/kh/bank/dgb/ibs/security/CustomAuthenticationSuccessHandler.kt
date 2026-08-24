package kh.bank.dgb.ibs.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

data class LoginResponseBody(
	val userID: String,
	val tkn: String,
)

/**
 * Port of `CustomAuthenticationSuccessHandler` — SIMPLIFIED per the confirmed CBS contract
 * (true/false only, no profile data). The old handler echoed a whole core-banking user profile
 * (`ATH0001ResDTO`) and checked `ServiceStatusService` for maintenance windows; there's no
 * profile to echo here since CBS doesn't provide one, and the service-status check is a separate
 * feature/DAO, not login mechanics — out of scope for this handler.
 *
 * `tkn = session id`, same convention as the old app, so nothing about the wire format changes
 * for existing clients. Session itself is Spring Session/Redis, established automatically once
 * this handler runs (SecurityContext gets persisted to it by Spring Security's session
 * management, same as before).
 */
@Component
class CustomAuthenticationSuccessHandler(
	private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {

	override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"
		val body = LoginResponseBody(userID = authentication.name, tkn = request.getSession(false)?.id.orEmpty())
		objectMapper.writeValue(
			response.writer,
			ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = body),
		)
	}
}
