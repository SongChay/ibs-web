package kh.bank.dgb.ibs.security.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator

/**
 * Port of `RSAPairKeyAuthenticationUtils` — one fresh 2048-bit RSA keypair per handshake
 * (issued by /rsa, private key kept server-side in session, public key handed to the client).
 */
object RsaKeyPairGenerator {

	private const val ALGORITHM = "RSA"
	private const val KEY_LENGTH = 2048

	fun generate(): KeyPair =
		KeyPairGenerator.getInstance(ALGORITHM).apply { initialize(KEY_LENGTH) }.genKeyPair()
}
