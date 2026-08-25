package kh.bank.dgb.ibs.configuration.filter

import jakarta.servlet.http.HttpServletRequest
import kh.bank.dgb.ibs.common.envelope.RequestData
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import tools.jackson.databind.ObjectMapper

data class LoginRequestBody(
	val userID: String? = null,
	val userPwd: String? = null,
)

/**
 * Port of the old `/security_check` login mechanics. The default `UsernamePasswordAuthenticationFilter`
 * reads username/password from request PARAMETERS (form-urlencoded), but this API is JSON-only
 * (`{"header": ..., "body": {"userID", "userPwd"}}`) — the old app worked around that with a
 * hand-rolled `AuthenticationDetailsSource` that re-parsed (and re-*decrypted*) the body itself.
 *
 * That decryption step is now redundant: `EncryptedEnvelopeFilter` already runs before Spring
 * Security and hands every downstream filter plain JSON regardless of whether the client
 * encrypted the request — so this filter can just read the body directly via the normal
 * `obtainUsername`/`obtainPassword` extension points, putting real values into
 * principal/credentials the standard way. No `AuthenticationDetailsSource` indirection needed.
 */
class JsonCredentialsAuthenticationFilter(
	authenticationManager: AuthenticationManager,
	private val objectMapper: ObjectMapper,
) : UsernamePasswordAuthenticationFilter(authenticationManager) {

	override fun obtainUsername(request: HttpServletRequest): String? = parsedBody(request).body?.userID

	override fun obtainPassword(request: HttpServletRequest): String? = parsedBody(request).body?.userPwd

	/** The request body can only be read once — obtainUsername/obtainPassword both need it, so
	 *  parse once and cache on the request. */
	private fun parsedBody(request: HttpServletRequest): RequestData<LoginRequestBody> {
		(request.getAttribute(PARSED_BODY_ATTR) as? RequestData<*>)?.let {
			@Suppress("UNCHECKED_CAST")
			return it as RequestData<LoginRequestBody>
		}
		val type = objectMapper.typeFactory.constructParametricType(RequestData::class.java, LoginRequestBody::class.java)
		@Suppress("UNCHECKED_CAST")
		val parsed = objectMapper.readValue<RequestData<LoginRequestBody>>(request.inputStream, type)
		request.setAttribute(PARSED_BODY_ATTR, parsed)
		return parsed
	}

	companion object {
		private const val PARSED_BODY_ATTR = "kh.bank.dgb.ibs.security.parsedLoginBody"
	}
}
