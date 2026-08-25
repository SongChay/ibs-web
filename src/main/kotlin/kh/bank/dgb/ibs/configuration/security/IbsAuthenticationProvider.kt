package kh.bank.dgb.ibs.configuration.security

import kh.bank.dgb.ibs.cbs.CoreBankingAuthClient
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/**
 * Port of `AuthenticationProviderImpl`. The old version called core banking's RSA endpoint,
 * RSA-encrypted the password, called the ATH0001 login API, and used its full response (user
 * profile, tokens) to build the Authentication. Per the confirmed simplified contract, CBS only
 * answers true/false — this provider owns 100% of session/Authentication construction itself;
 * `CoreBankingAuthClient` is consulted purely as a yes/no gate.
 *
 * Reads credentials from `authentication.principal`/`credentials` directly — populated by
 * `JsonCredentialsAuthenticationFilter`'s `obtainUsername`/`obtainPassword` overrides, no need
 * for the old app's `AuthenticationDetailsSource` indirection (that existed only to smuggle
 * JSON-body values past the default form-param-reading filter).
 */
@Component
class IbsAuthenticationProvider(
	private val coreBankingAuthClient: CoreBankingAuthClient,
) : AuthenticationProvider {

	override fun authenticate(authentication: Authentication): Authentication {
		val userId = authentication.principal as? String
		val password = authentication.credentials as? String

		if (userId.isNullOrBlank() || password.isNullOrBlank()) {
			throw BadCredentialsException("Missing userID or userPwd")
		}

		if (!coreBankingAuthClient.verifyCredentials(userId, password)) {
			throw BadCredentialsException("Invalid username or password")
		}

		return UsernamePasswordAuthenticationToken(userId, null, listOf(SimpleGrantedAuthority("ROLE_USER")))
	}

	override fun supports(authentication: Class<*>): Boolean =
		UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}
