package kh.bank.dgb.ibs.configuration.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.app.local.service_status.ServiceStatusSbc
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import kh.bank.dgb.ibs.configuration.security.IbsAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Port of `CustomAuthenticationSuccessHandler` — restored to echo the full CBS login profile
 * (`Ath0001Response`, carried by `IbsAuthenticationToken`) rather than the `{userID, tkn}` body
 * used while CBS auth was a bare true/false gate.
 *
 * Ported behaviors:
 *  - Whole-app maintenance gate (`ServiceStatusSbc.isServiceOff`) — checked AFTER credentials are
 *    already verified, so valid credentials still get turned away with a "service unavailable"
 *    error while this is on, exactly like the old handler.
 *  - `lastLoginDate`/`lastLoginHms` reformatted for display (old `DateUtil.toDDMMMYYYY`/`toHHMMA`).
 *  - `tkn` set to the session id.
 *  - Full profile stored in session under the old `USR_SESSION_KEY` ("userSession") name, for
 *    parity with the old app even though nothing else in this port currently reads it back out
 *    that way (`UserIdValidationFilter` reads the equivalent userID via `SecurityContext`, which
 *    is persisted into the same session regardless).
 *
 * NOT re-ported: the old handler's own AES-encryption of the response body. `EncryptedEnvelopeFilter`
 * already wraps every response uniformly and re-encrypts `body` whenever `header.result == true` —
 * this handler only needs to write plain JSON and let that filter do its job, same as every other
 * endpoint in this app.
 *
 * Session-handling fixes already in place here (unrelated to this restoration, kept as-is): forces
 * session creation before writing the response body, and explicitly persists the `SecurityContext`
 * since Spring Security 6+'s `SecurityContextHolderFilter` only loads context, never saves it
 * (unlike the old app's Spring Security 4 `SecurityContextPersistenceFilter`).
 */
@Component
class CustomAuthenticationSuccessHandler(
	private val serviceStatusSbc: ServiceStatusSbc,
	private val objectMapper: ObjectMapper,
) : AuthenticationSuccessHandler {

	private val securityContextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()

	override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
		val token = authentication as IbsAuthenticationToken
		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"

		val serviceOff = serviceStatusSbc.isServiceOff()
		if (serviceOff.off) {
			val header = ResponseResultUtils.makeResponse(
				false,
				ResponseResultCodeType.SERVICE_STATUS_OFF.value,
				serviceOff.description ?: ResponseResultCodeType.SERVICE_STATUS_OFF.description,
			)
			objectMapper.writeValue(response.writer, ResponseData<Unit>(header = header))
			return
		}

		val session = request.getSession(true)
		securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response)

		val profile = token.profile.copy(
			lastLoginDate = formatLastLoginDate(token.profile.lastLoginDate),
			lastLoginHms = formatLastLoginHms(token.profile.lastLoginHms),
			tkn = session.id,
		)
		session.setAttribute(USR_SESSION_KEY, profile)

		val header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS)
		objectMapper.writeValue(response.writer, ResponseData(header = header, body = profile))
	}

	/** Port of `DateUtil.toDDMMMYYYY` — old CBS date format `yyyyMMdd` -> display `dd MMM yyyy`. */
	private fun formatLastLoginDate(raw: String?): String? {
		if (raw.isNullOrBlank()) return raw
		return runCatching {
			LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
		}.getOrElse { raw }
	}

	/** Port of `DateUtil.toHHMMA` — old CBS time format (only the first 4 digits, `HHmm`, are
	 *  used) -> display `hh:mm a`. */
	private fun formatLastLoginHms(raw: String?): String? {
		if (raw.isNullOrBlank()) return raw
		return runCatching {
			LocalTime.parse(raw.take(4), DateTimeFormatter.ofPattern("HHmm")).format(DateTimeFormatter.ofPattern("hh:mm a"))
		}.getOrElse { raw }
	}

	companion object {
		/** Port of `BizResultCodeType.USR_SESSION_KEY`. */
		const val USR_SESSION_KEY = "userSession"
	}
}
