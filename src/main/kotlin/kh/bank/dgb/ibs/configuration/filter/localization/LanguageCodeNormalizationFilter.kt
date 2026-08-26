package kh.bank.dgb.ibs.configuration.filter.localization

import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * Port of `MainController.localization(String)`. Every request's `header.languageCode` arrives as
 * a raw two-digit code ("01" EN / "02" KM / "03" KO / "04" JA / "05" ZH) and must be normalized to
 * the language tag CBS and the rest of this app expect before dispatch — the old MainController
 * did this inline, once, right after parsing the header, for every dispatch path (encrypted or
 * not, including the login call itself) before ever reaching the adapter. Centralized here the
 * same way, as its own filter (mirroring how `EncryptedEnvelopeFilter` centralizes the crypto
 * concern) rather than duplicated per-controller.
 *
 * `header` always travels in clear even on encrypted requests (only `body` is ciphertext — see
 * `EncryptedEnvelopeFilter`), so this filter doesn't need to run on either side of decryption to
 * see it; ordered right after `EncryptedEnvelopeFilter` (-200) purely for readability of the
 * request-mutating filter sequence, and before Spring Security's own filter chain (-100) so the
 * login request is normalized too, matching the old app's single dispatch path covering login the
 * same as every other adapter call.
 */
@Component
@Order(-190)
class LanguageCodeNormalizationFilter(
	private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		// Multipart requests (file uploads) have no JSON `header.languageCode` to normalize, and
		// reading their body here would drain the raw input stream before Tomcat's own multipart
		// parser gets a chance to read it — breaking every file upload. Found live: uploading
		// through `/upload/companyProfile` threw `MissingServletRequestPartException` until this
		// guard was added.
		if (request.method != "POST" || request.contentType?.startsWith("multipart/", ignoreCase = true) == true) {
			filterChain.doFilter(request, response)
			return
		}

		val bytes = request.inputStream.readBytes()
		if (bytes.isEmpty()) {
			filterChain.doFilter(ReplayableBodyRequestWrapper(request, bytes), response)
			return
		}

		val node = runCatching { objectMapper.readTree(bytes) }.getOrNull() as? ObjectNode
		val header = node?.get(HEADER_FIELD) as? ObjectNode
		if (node == null || header == null) {
			filterChain.doFilter(ReplayableBodyRequestWrapper(request, bytes), response)
			return
		}

		val rawCode = header.path("languageCode").let { if (it.isString) it.asString() else null }
		header.put("languageCode", normalize(rawCode))
		filterChain.doFilter(ReplayableBodyRequestWrapper(request, objectMapper.writeValueAsBytes(node)), response)
	}

	private fun normalize(code: String?): String {
		return when (code) {
			"01" -> "EN"
			"02" -> "KM"
			"03" -> "KO"
			"04" -> "JA"
			"05" -> "ZH"
			else -> "EN"
		}
	}

	/**
	 * Shared plumbing for the small family of request-body-inspecting filters (this one and
	 * `UserIdValidationFilter`) that need to read the full request body once, optionally rewrite
	 * it, and hand a fresh replayable stream to the rest of the filter chain / controller.
	 *
	 * Nested here (not `private`) rather than a standalone file, since this filter is its primary
	 * owner; `UserIdValidationFilter` — in the sibling `authorization` package — reaches in via
	 * `LanguageCodeNormalizationFilter.ReplayableBodyRequestWrapper` rather than getting its own
	 * copy. `DecryptedBodyRequestWrapper` is a separate, unrelated class nested in
	 * `EncryptedEnvelopeFilter` — that one has exactly one user, so it stays private there.
	 */
	class ReplayableBodyRequestWrapper(request: HttpServletRequest, private val body: ByteArray) : HttpServletRequestWrapper(request) {

		override fun getInputStream(): ServletInputStream {
			val byteStream = ByteArrayInputStream(body)
			return object : ServletInputStream() {
				override fun read(): Int {
					return byteStream.read()
				}

				override fun isFinished(): Boolean {
					return byteStream.available() == 0
				}

				override fun isReady(): Boolean {
					return true
				}

				override fun setReadListener(readListener: ReadListener?) {
				}
			}
		}

		override fun getReader(): BufferedReader {
			return BufferedReader(InputStreamReader(inputStream, characterEncoding ?: "UTF-8"))
		}
	}

	companion object {
		private const val HEADER_FIELD = "header"
	}
}
