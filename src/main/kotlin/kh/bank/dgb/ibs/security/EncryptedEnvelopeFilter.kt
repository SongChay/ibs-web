package kh.bank.dgb.ibs.security

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kh.bank.dgb.ibs.security.crypto.AesEncryptionUtils
import kh.bank.dgb.ibs.security.crypto.CryptoSessionKeys
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper

/**
 * Port of the encryption half of the old `MainController` (bizmob.corpbanking.platform.controller).
 * The reflective adapter-dispatch itself is gone — each adapter is now a real `@RestController` —
 * but the wire-level AES envelope encryption is still required for existing clients, so it's
 * pulled out here and applied uniformly instead of duplicated per controller.
 *
 * Wire contract preserved exactly:
 *  - Request/response JSON is always `{"header": ..., "body": ...}`.
 *  - The `Content-MD5` request header (reused as a boolean on/off flag, NOT an actual content
 *    hash — an inherited quirk from the old app, not our naming) turns encryption on for this
 *    request.
 *  - When on, "body" arrives as a hex(IV)+hex(ciphertext) STRING, decrypted here into real JSON
 *    before the controller ever sees it (see DecryptedBodyRequestWrapper); "header" always
 *    travels in clear.
 *  - The AES key was established earlier via /rsa + /aes and lives in the session.
 *  - Response body is re-encrypted the same way, but ONLY when `header.result == true` —
 *    error bodies (produced by GlobalExceptionHandler, or written directly by the security
 *    handlers) are sent in clear. This mirrors the old MainController, whose catch blocks
 *    returned a ResponseData without ever calling MRAesUtils.encryptAES.
 *  - HTTP status is always 200 throughout this app (confirmed decision) — success/failure is
 *    conveyed by `header.result`/`resultCode`, never by the transport status. That's *why* the
 *    encrypt decision below reads the body's `header.result` instead of the response status:
 *    the status carries no signal here.
 *
 * Ordered to run BEFORE Spring Security's filter chain (Boot registers it at order -100, so -200
 * runs earlier) so that by the time the login flow (once built) reads the request body, it's
 * already plain JSON — see the old `UserWebAuthenticationDetails`, which had to duplicate this
 * same AES-decrypt logic itself specifically because nothing upstream of Spring Security did it
 * for it. With this filter running first, that duplication goes away.
 */
@Component
@Order(-200)
class EncryptedEnvelopeFilter(
	private val objectMapper: ObjectMapper,
	@Value("\${ibs.security.encryption-required:false}") private val encryptionRequired: Boolean,
) : OncePerRequestFilter() {

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		val encryptionRequested = request.getHeader(ENCRYPTION_HEADER).toBoolean()

		if (!encryptionRequested) {
			if (encryptionRequired) {
				writeUnencryptedError("CBK_ERR0000", "An unknown error occurred.", response)
				return
			}
			filterChain.doFilter(request, response)
			return
		}

		val aesKey = request.getSession(false)?.getAttribute(CryptoSessionKeys.AES_SECRET_KEY) as? String
		if (aesKey == null) {
			writeUnencryptedError("CBK_401", "Unauthorized request", response)
			return
		}

		val decryptedRequest = DecryptedBodyRequestWrapper(request, aesKey, objectMapper)
		val cachingResponse = ContentCachingResponseWrapper(response)

		filterChain.doFilter(decryptedRequest, cachingResponse)

		val buffered = cachingResponse.contentAsByteArray
		if (buffered.isEmpty()) {
			cachingResponse.copyBodyToResponse()
			return
		}

		val node = objectMapper.readTree(buffered) as ObjectNode
		val succeeded = node.path("header").path("result").let { it.isBoolean && it.asBoolean() }
		val bodyNode = node.get(BODY_FIELD)

		if (!succeeded || bodyNode == null || bodyNode.isNull) {
			cachingResponse.copyBodyToResponse()
			return
		}

		node.put(BODY_FIELD, AesEncryptionUtils.encrypt(bodyNode.toString(), aesKey))
		val rewritten = objectMapper.writeValueAsBytes(node)
		response.setContentLength(rewritten.size)
		response.outputStream.write(rewritten)
	}

	/** Error path bypasses the response wrapper entirely — nothing to decrypt/encrypt since the
	 *  request never reached a controller. Always HTTP 200, per the status-convention decision. */
	private fun writeUnencryptedError(resultCode: String, resultMessage: String, response: HttpServletResponse) {
		response.status = HttpServletResponse.SC_OK
		response.contentType = "application/json;charset=UTF-8"
		response.writer.write(
			objectMapper.writeValueAsString(
				mapOf("header" to mapOf("result" to false, "resultCode" to resultCode, "resultMessage" to resultMessage)),
			),
		)
	}

	companion object {
		private const val ENCRYPTION_HEADER = "Content-MD5"
		private const val BODY_FIELD = "body"
	}
}
