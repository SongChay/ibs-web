package kh.bank.dgb.ibs.configuration.controller

import jakarta.servlet.http.HttpSession
import kh.bank.dgb.ibs.configuration.security.crypto.CryptoSessionKeys
import kh.bank.dgb.ibs.configuration.security.crypto.RsaKeyPairGenerator
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Base64

data class RsaHandshakeResponse(
	val token: String,
	val publicKey: String,
)

/**
 * Port of the old `/RSA` adapter (RSA_Adapter.java) — first step of the crypto handshake.
 * Issues a fresh RSA keypair, keeps the private key server-side in the session, hands the
 * client the public key (Base64-encoded, X.509 SubjectPublicKeyInfo) to encrypt its AES
 * passphrase with on the next call to /aes.
 *
 * Clears any prior session state first, same as the original — re-hitting /rsa starts a brand
 * new handshake rather than layering on top of an old one.
 */
@RestController
class RsaHandshakeController {

	@PostMapping("/rsa")
	fun issueKeyPair(session: HttpSession): RsaHandshakeResponse {
		session.attributeNames.toList().forEach(session::removeAttribute)

		val keyPair = RsaKeyPairGenerator.generate()
		session.setAttribute(CryptoSessionKeys.RSA_PRIVATE_KEY, keyPair.private)

		return RsaHandshakeResponse(
			token = session.id,
			publicKey = Base64.getEncoder().encodeToString(keyPair.public.encoded),
		)
	}
}
