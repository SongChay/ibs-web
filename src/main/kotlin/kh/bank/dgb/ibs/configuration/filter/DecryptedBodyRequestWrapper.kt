package kh.bank.dgb.ibs.configuration.filter

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.node.ObjectNode
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import kh.bank.dgb.ibs.configuration.security.handshake.aes.AesEncryptionUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

/**
 * Wraps an incoming request whose JSON `"body"` field is AES ciphertext (a hex string) and
 * replaces it with the decrypted JSON *before* the controller's `@RequestBody` deserialization
 * runs — so `RequestData<T>` binding sees plain JSON exactly like an unencrypted call. `"header"`
 * is left untouched; it was never encrypted in the old protocol either.
 */
class DecryptedBodyRequestWrapper(
	request: HttpServletRequest,
	aesKey: String,
	objectMapper: ObjectMapper,
) : HttpServletRequestWrapper(request) {

	private val rewrittenBody: ByteArray = run {
		val node = objectMapper.readTree(request.inputStream) as ObjectNode
		val bodyNode = node.get("body")
		if (bodyNode != null && bodyNode.isString) {
			val decrypted = AesEncryptionUtils.decrypt(bodyNode.asString(), aesKey)
			node.replace("body", objectMapper.readTree(decrypted))
		}
		objectMapper.writeValueAsBytes(node)
	}

	override fun getInputStream(): ServletInputStream {
		val byteStream = ByteArrayInputStream(rewrittenBody)
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
