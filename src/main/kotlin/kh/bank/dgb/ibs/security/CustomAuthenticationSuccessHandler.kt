package kh.bank.dgb.ibs.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
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
 * for existing clients.
 *
 * TWO BUGS FIXED HERE, both around session handling that the old app never had to think about
 * (it ran on Spring Security 4's `SecurityContextPersistenceFilter`, which auto-saved on every
 * request; Spring Security 6+'s `SecurityContextHolderFilter` only *loads* context, it never
 * saves):
 *  1. `request.getSession(false)` returned null and writing the response body committed it
 *     before anything downstream could add a `Set-Cookie` header — no client ever got a usable
 *     session at all. Fixed by forcing session creation with `getSession(true)` before writing.
 *  2. Even with a session created, the authenticated `SecurityContext` was never actually
 *     persisted into it — every subsequent request came back unauthenticated. Fixed by saving it
 *     explicitly via `SecurityContextRepository`, the same mechanism `SecurityContextHolderFilter`
 *     uses to *read* it back on the next request.
 */
@Component
class CustomAuthenticationSuccessHandler(
	private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {

	private val securityContextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()

	override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
		val session = request.getSession(true)
		securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response)

		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"
		val body = LoginResponseBody(userID = authentication.name, tkn = session.id)
		objectMapper.writeValue(
			response.writer,
			ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = body),
		)
	}
}
