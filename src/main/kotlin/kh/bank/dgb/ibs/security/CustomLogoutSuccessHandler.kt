package kh.bank.dgb.ibs.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

/** Port of `CustomLogoutSuccessHandler`. */
@Component
class CustomLogoutSuccessHandler(
	private val responseWriter: SecurityResponseWriter,
) : LogoutSuccessHandler {

	override fun onLogoutSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
		if (response.isCommitted) return
		responseWriter.write(response, ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS))
	}
}
