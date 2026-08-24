package kh.bank.dgb.ibs.security.crypto

import java.security.Key
import java.util.Base64
import javax.crypto.Cipher

/**
 * Port of the old `RSAEncryptionUtils` — used only for the one-time handshake where the client
 * sends its AES passphrase RSA-encrypted with the public key /rsa handed out. Base64 wire format
 * preserved from the original.
 */
object RsaEncryptionUtils {

	private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"

	fun decrypt(base64CipherText: String, key: Key): String {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		cipher.init(Cipher.DECRYPT_MODE, key)
		return String(cipher.doFinal(Base64.getDecoder().decode(base64CipherText)), Charsets.UTF_8)
	}

	fun encrypt(plainText: String, key: Key): String {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		cipher.init(Cipher.ENCRYPT_MODE, key)
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)))
	}
}
