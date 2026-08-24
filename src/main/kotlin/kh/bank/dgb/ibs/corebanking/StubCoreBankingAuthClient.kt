package kh.bank.dgb.ibs.corebanking

import org.springframework.stereotype.Component

/**
 * Always reports success — confirmed decision, not a placeholder pending follow-up work: the
 * real RSA-key-exchange + credential-check HTTP integration (see the old
 * `AuthenticationProviderImpl`/`ChannelRSAUtil`) is explicitly out of scope for this rewrite.
 * If that ever changes, swap this bean out for a real implementation — `IbsAuthenticationProvider`
 * only depends on the `CoreBankingAuthClient` interface, so nothing else in the login flow would
 * need to change.
 */
@Component
class StubCoreBankingAuthClient : CoreBankingAuthClient {
	override fun verifyCredentials(userId: String, password: String): Boolean = true
}
