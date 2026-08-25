package kh.bank.dgb.ibs.cbs

/**
 * Boundary to the core-banking system (CBS) for the real login flow: RSA-encrypt the submitted
 * password using CBS's own RSA key exchange, then call CBS's ATH0001 login opcode. On success,
 * CBS hands back a full corporate-user profile (used to populate this app's session and echoed to
 * the client on login) — NOT a bare true/false.
 *
 * Restored from an earlier, deliberately simplified true/false-only contract (`verifyCredentials`)
 * that treated CBS purely as a credential gate. That was a confirmed decision at the time, but it
 * dropped real behavior the old app depended on (see `AuthenticationProviderImpl`/`ATH0001ResDTO`
 * for the original) — this interface and `DefaultCoreBankingAuthClient` restore it. See
 * `IbsAuthenticationProvider` for how the result becomes a Spring Security `Authentication`.
 */
fun interface CoreBankingAuthClient {
	fun authenticate(userId: String, password: String, channelTypeCode: String, languageCode: String?): Ath0001Result
}
