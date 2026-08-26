package kh.bank.dgb.ibs.configuration.security.crypto

import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec

/**
 * Port of `ChannelRSAUtil`'s public-key reconstruction — CBS hands back its RSA public key as a
 * hex (radix-16) modulus/exponent pair, not a Base64 X.509 blob like this app's own client-facing
 * `/rsa` handshake, so it needs its own small builder before `RsaEncryptionUtils.encrypt` (same
 * `RSA/ECB/PKCS1Padding` transformation, already shared) can use it.
 */
object ChannelRsaUtils {

	private const val RADIX = 16

	fun publicKeyFrom(modulusHex: String, exponentHex: String): PublicKey {
		val spec = RSAPublicKeySpec(BigInteger(modulusHex, RADIX), BigInteger(exponentHex, RADIX))
		return KeyFactory.getInstance("RSA").generatePublic(spec)
	}

	fun encrypt(plainText: String, modulusHex: String, exponentHex: String): String {
		return RsaEncryptionUtils.encrypt(plainText, publicKeyFrom(modulusHex, exponentHex))
	}
}
