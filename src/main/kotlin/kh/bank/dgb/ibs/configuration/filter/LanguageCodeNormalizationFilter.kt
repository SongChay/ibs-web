package kh.bank.dgb.ibs.configuration.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode

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
		if (request.method != "POST") {
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

	private fun normalize(code: String?): String = when (code) {
		"01" -> "EN"
		"02" -> "KM"
		"03" -> "KO"
		"04" -> "JA"
		"05" -> "ZH"
		else -> "EN"
	}

	companion object {
		private const val HEADER_FIELD = "header"
	}
}
