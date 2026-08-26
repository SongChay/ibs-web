package kh.bank.dgb.ibs.configuration.security.handshake

/**
 * Session attribute names for the RSA→AES handshake, carried over unchanged from the old
 * `BizResultCodeType` session-key constants (`rsaCipher`, `aesCipher`) so nothing about the
 * session shape changes — only where the code that reads/writes them lives. Kept at this shared
 * `handshake` level (not nested under `aes`/`rsa`) since both subpackages read it.
 */
object CryptoSessionKeys {
	const val RSA_PRIVATE_KEY = "rsaCipher"
	const val AES_SECRET_KEY = "aesCipher"
}
