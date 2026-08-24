package kh.bank.dgb.ibs.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.web.session.InvalidSessionStrategy
import org.springframework.stereotype.Component

/**
 * Port of `InvalidSessionStrategyHandler` — fires when a request carries a session id the
 * container/Redis no longer recognizes (expired or evicted). Only responds when the client
 * actually sent a now-invalid session id, same guard as the original: a request with no session
 * id at all isn't "invalid", it's just anonymous.
 */
@Component
class InvalidSessionStrategyHandler(
	private val responseWriter: SecurityResponseWriter,
) : InvalidSessionStrategy {

	override fun onInvalidSessionDetected(request: HttpServletRequest, response: HttpServletResponse) {
		if (!request.requestedSessionId.isNullOrEmpty() && !request.isRequestedSessionIdValid) {
			responseWriter.write(response, ResponseResultUtils.makeResponse(false, ResponseResultCodeType.UNAUTHORIZED_REQUEST))
		}
	}
}
