package kh.bank.dgb.ibs.configuration.controller

import jakarta.servlet.http.HttpSession
import kh.bank.dgb.ibs.common.exception.UnauthorizedException
import kh.bank.dgb.ibs.configuration.security.handshake.CryptoSessionKeys
import kh.bank.dgb.ibs.configuration.security.handshake.rsa.RsaEncryptionUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.PrivateKey

data class AesHandshakeRequest(
	val encodedKey: String,
)

/**
 * Port of the old `/AES` adapter (AES_Adapter.java) — second step of the crypto handshake.
 * Client RSA-encrypts a 128-character AES passphrase with the public key from /RSA and posts
 * it here; we decrypt it with the session's RSA private key and remember it as the AES key
 * used to encrypt/decrypt every subsequent request/response body.
 *
 * Route is `/AES`, uppercase — confirmed against the real client (its compiled JS literally posts
 * to `/AES`), not the lowercase `/aes` this was originally ported as; Spring's path matching is
 * case-sensitive, so this mattered.
 */
@RestController
class AesHandshakeController {

	@PostMapping("/AES")
	fun exchangeKey(@RequestBody request: AesHandshakeRequest, session: HttpSession) {
		val privateKey = session.getAttribute(CryptoSessionKeys.RSA_PRIVATE_KEY) as? PrivateKey
			?: throw UnauthorizedException("No RSA handshake in progress for this session")

		val secretKey = RsaEncryptionUtils.decrypt(request.encodedKey, privateKey)
		if (secretKey.length != 128) {
			throw UnauthorizedException("Decrypted AES key must be 128 characters")
		}

		session.setAttribute(CryptoSessionKeys.AES_SECRET_KEY, secretKey)
	}
}
