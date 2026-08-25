package kh.bank.dgb.ibs.cbs

/**
 * Boundary to the core-banking system (CBS) for credential verification. Confirmed contract:
 * CBS answers true/false only — no session tokens, no user profile. Everything else about the
 * authenticated session (Spring Session/Redis, roles, response shape) is this app's own
 * responsibility — see `IbsAuthenticationProvider`.
 */
fun interface CoreBankingAuthClient {
	fun verifyCredentials(userId: String, password: String): Boolean
}
