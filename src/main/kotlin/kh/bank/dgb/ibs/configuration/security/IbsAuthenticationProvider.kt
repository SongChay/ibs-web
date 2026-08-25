package kh.bank.dgb.ibs.configuration.security

import kh.bank.dgb.ibs.cbs.Ath0001Result
import kh.bank.dgb.ibs.cbs.CoreBankingAuthClient
import kh.bank.dgb.ibs.configuration.filter.LoginRequestDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/** Port of `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING`. */
private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"

/**
 * Port of `AuthenticationProviderImpl` — restored to the original CBS contract (full corporate
 * user profile on success, not a bare true/false; see `CoreBankingAuthClient`).
 *
 * Reads userID/password from `authentication.principal`/`credentials` directly — populated by
 * `JsonCredentialsAuthenticationFilter`'s `obtainUsername`/`obtainPassword` overrides — and
 * languageCode from `authentication.details` (populated by that same filter's custom
 * `AuthenticationDetailsSource`), the Kotlin equivalent of the old app's
 * `UserWebAuthenticationDetails` indirection.
 *
 * Not ported: the old code additionally BCrypt-hashed the (already RSA-encrypted) password into
 * the resulting token's `credentials` field. Nothing ever read that value back — Spring Security
 * erases `credentials` after successful authentication by default anyway — so it's dropped here
 * rather than reproduced as dead weight.
 */
@Component
class IbsAuthenticationProvider(
	private val coreBankingAuthClient: CoreBankingAuthClient,
) : AuthenticationProvider {

	override fun authenticate(authentication: Authentication): Authentication {
		val userId = authentication.principal as? String
		val password = authentication.credentials as? String
		val languageCode = (authentication.details as? LoginRequestDetails)?.languageCode

		if (userId.isNullOrBlank() || password.isNullOrBlank()) {
			throw BadCredentialsException("Missing userID or userPwd")
		}

		return when (val result = coreBankingAuthClient.authenticate(userId, password, CHANNEL_TYPE_CODE_CORP_BANKING, languageCode)) {
			is Ath0001Result.Success -> IbsAuthenticationToken(
				principal = result.profile.userID ?: userId,
				// Credentials are erased immediately after successful authentication anyway (Spring
				// Security's default `eraseCredentialsAfterAuthentication` behavior) — nothing ever
				// reads this back, so an empty placeholder is enough; see the class doc comment.
				credentials = "",
				authorities = listOf(SimpleGrantedAuthority("ROLE_USER")),
				resHeader = result.header,
				profile = result.profile,
			)
			is Ath0001Result.Failure -> throw CoreBankingAuthenticationException(result.resultCode, result.resultMessage)
		}
	}

	override fun supports(authentication: Class<*>): Boolean =
		UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}
