package kh.bank.dgb.ibs.security.crypto

import java.security.MessageDigest
import java.util.HexFormat
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Port of the old `MRAesUtils` (bizmob.corpbanking.common.util.MRAesUtils).
 *
 * Wire format preserved exactly for compatibility with anything already speaking this protocol:
 * AES/CBC/PKCS5Padding, key = SHA-1(passphrase) truncated to 16 bytes (AES-128) — so the "AES key"
 * exchanged over /aes is really a passphrase, not a raw key. Payload is hex(IV) + hex(ciphertext)
 * concatenated with no separator; IV is always 16 bytes (32 hex chars), which is how decrypt()
 * knows where to split. Old code produced uppercase hex (`DatatypeConverter.printHexBinary`) —
 * kept uppercase here too, though parsing accepts either case.
 */
object AesEncryptionUtils {

	private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
	private const val KEY_ALGORITHM = "AES"
	private const val IV_HEX_LENGTH = 32 // 16-byte IV, hex-encoded
	private val HEX = HexFormat.of().withUpperCase()

	private fun deriveKey(passphrase: String): SecretKeySpec {
		val digest = MessageDigest.getInstance("SHA-1").digest(passphrase.toByteArray(Charsets.UTF_8))
		return SecretKeySpec(digest.copyOf(16), KEY_ALGORITHM)
	}

	fun encrypt(plainText: String, passphrase: String): String {
		val cipher = Cipher.getInstance(TRANSFORMATION)
		cipher.init(Cipher.ENCRYPT_MODE, deriveKey(passphrase))
		val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
		return HEX.formatHex(cipher.iv) + HEX.formatHex(cipherText)
	}

	fun decrypt(hexPayload: String, passphrase: String): String {
		val iv = HEX.parseHex(hexPayload.substring(0, IV_HEX_LENGTH))
		val cipherText = HEX.parseHex(hexPayload.substring(IV_HEX_LENGTH))
		val cipher = Cipher.getInstance(TRANSFORMATION)
		cipher.init(Cipher.DECRYPT_MODE, deriveKey(passphrase), IvParameterSpec(iv))
		return String(cipher.doFinal(cipherText), Charsets.UTF_8)
	}
}
