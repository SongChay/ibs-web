package kh.bank.dgb.ibs.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode

/**
 * Port of `MainController.validateUserId` / `checkUserId` / `USER_ID_CHECK_LIST`. A small,
 * hardcoded whitelist of sensitive routes requires the session's authenticated user ID to match
 * BOTH the request header's `userID` AND the request body's `userID` — a defense against a
 * forged/mismatched user ID being used against a different session's transfer or account data.
 * Every other route skips this check entirely, exactly as in the old code. This is a security
 * check the platform-level `MainController` used to enforce for every one of these adapters
 * regardless of which one handled the request; now that each adapter is its own `@RestController`,
 * it has to live somewhere shared again rather than be re-added to 4 separate controllers.
 *
 * Old code drew a distinction the port preserves: no authenticated user at all -> `INVALID_REQUEST`
 * (`user == null` branch); an authenticated user whose ID doesn't match header/body -> the more
 * specific `UNAUTHORIZED_REQUEST`.
 *
 * Ordered at -50 — after Spring Security's own filter chain (-100) so `SecurityContextHolder`
 * already holds the request's restored `Authentication` by the time this runs, but still ahead of
 * the controller. `authorizeHttpRequests` in `SecurityConfig` already requires authentication for
 * every route not explicitly whitelisted there (all 4 of these routes are not whitelisted), so in
 * practice this filter only ever sees an authenticated, non-anonymous principal for these paths —
 * the anonymous/null check below is defensive, not load-bearing.
 */
@Component
@Order(-50)
class UserIdValidationFilter(
	private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		if (request.servletPath !in USER_ID_CHECK_LIST) {
			filterChain.doFilter(request, response)
			return
		}

		val authentication = SecurityContextHolder.getContext().authentication
		val sessionUserId = authentication
			?.takeIf { it.isAuthenticated && it !is AnonymousAuthenticationToken }
			?.name

		if (sessionUserId == null) {
			writeError(response, ResponseResultCodeType.INVALID_REQUEST)
			return
		}

		val bytes = request.inputStream.readBytes()
		val node = runCatching { objectMapper.readTree(bytes) }.getOrNull() as? ObjectNode

		val headerUserId = node?.path("header")?.path("userID")?.let { if (it.isString) it.asString() else null }
		val bodyUserId = node?.path("body")?.path("userID")?.let { if (it.isString) it.asString() else null }

		if (sessionUserId != headerUserId || sessionUserId != bodyUserId) {
			writeError(response, ResponseResultCodeType.UNAUTHORIZED_REQUEST)
			return
		}

		filterChain.doFilter(ReplayableBodyRequestWrapper(request, bytes), response)
	}

	/** Always HTTP 200, per the app-wide status convention — the request never reaches a
	 *  controller, so nothing here needs encryption either (matches `EncryptedEnvelopeFilter`'s own
	 *  error paths, which also bypass encryption). */
	private fun writeError(response: HttpServletResponse, code: ResponseResultCodeType) {
		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"
		response.writer.write(
			objectMapper.writeValueAsString(
				mapOf(
					"header" to mapOf(
						"result" to false,
						"resultCode" to code.value,
						"resultMessage" to code.description,
					),
				),
			),
		)
	}

	companion object {
		private val USER_ID_CHECK_LIST = setOf("/TRS1001", "/TRS1102", "/ACI1002", "/ACI1006")
	}
}
