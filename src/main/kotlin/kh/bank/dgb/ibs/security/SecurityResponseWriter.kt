package kh.bank.dgb.ibs.security

import tools.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo
import org.springframework.stereotype.Component

/**
 * Shared "write a ResponseData envelope directly to the response" helper for the hand-written
 * security handlers (entry point, session strategies, failure/logout handlers) — these all run
 * outside normal MVC dispatch, so they can't just return a DTO like a `@RestController` and rely
 * on Jackson's HttpMessageConverter; they have to write the response body themselves, same as
 * the old app's handlers did.
 *
 * Always HTTP 200 — result is conveyed in `header.result`/`resultCode`, per the confirmed
 * status-convention decision (see GlobalExceptionHandler).
 */
@Component
class SecurityResponseWriter(private val objectMapper: ObjectMapper) {

	fun write(response: HttpServletResponse, header: ResponseUserHeaderVo) {
		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"
		objectMapper.writeValue(response.writer, ResponseData<Unit>(header = header))
	}
}
