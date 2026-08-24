package kh.bank.dgb.ibs.security

import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.web.session.SessionInformationExpiredEvent
import org.springframework.security.web.session.SessionInformationExpiredStrategy
import org.springframework.stereotype.Component

/**
 * Port of `ExpireSessionStrategyHandler` (renamed for grammar; same class) — fires when a
 * session is kicked off by the single-session concurrency control because a newer login from
 * elsewhere won the slot.
 */
@Component
class ExpiredSessionStrategyHandler(
	private val responseWriter: SecurityResponseWriter,
) : SessionInformationExpiredStrategy {

	override fun onExpiredSessionDetected(event: SessionInformationExpiredEvent) {
		event.sessionInformation.expireNow()
		responseWriter.write(event.response, ResponseResultUtils.makeResponse(false, ResponseResultCodeType.SESSION_MAX_COUNT))
	}
}
