package kh.bank.dgb.ibs.security

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * Shared plumbing for the small family of request-body-inspecting filters
 * (`LanguageCodeNormalizationFilter`, `UserIdValidationFilter` — and conceptually
 * `DecryptedBodyRequestWrapper`, which predates this and stays as its own class) that need to read
 * the full request body once, optionally rewrite it, and hand a fresh replayable stream to the
 * rest of the filter chain / controller.
 */
class ReplayableBodyRequestWrapper(request: HttpServletRequest, private val body: ByteArray) : HttpServletRequestWrapper(request) {

	override fun getInputStream(): ServletInputStream {
		val byteStream = ByteArrayInputStream(body)
		return object : ServletInputStream() {
			override fun read(): Int = byteStream.read()
			override fun isFinished(): Boolean = byteStream.available() == 0
			override fun isReady(): Boolean = true
			override fun setReadListener(readListener: ReadListener?) = Unit
		}
	}

	override fun getReader(): BufferedReader =
		BufferedReader(InputStreamReader(inputStream, characterEncoding ?: "UTF-8"))
}
