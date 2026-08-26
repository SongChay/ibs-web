package kh.bank.dgb.ibs.configuration.security.provider

import org.springframework.security.core.AuthenticationException

/** Port of `CustomAuthenticationException` — carries CBS's own (already message-substituted)
 *  result code/message straight through to `CustomAuthenticationFailureHandler`, instead of
 *  collapsing every CBS login failure into a generic `BadCredentialsException`. */
class CoreBankingAuthenticationException(
	val resultCode: String?,
	val resultMessage: String?,
) : AuthenticationException(resultMessage)
