package kh.bank.dgb.ibs.configuration.security

import kh.bank.dgb.ibs.cbs.Ath0001Response
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * Port of `AuthenticationToken` — carries the full CBS login profile alongside the standard
 * Spring Security principal/authorities, so `CustomAuthenticationSuccessHandler` can echo it to
 * the client and store it in session exactly like the old app did.
 */
class IbsAuthenticationToken(
	principal: Any,
	credentials: Any,
	authorities: Collection<GrantedAuthority>,
	val resHeader: ResponseUserHeaderVo,
	val profile: Ath0001Response,
) : UsernamePasswordAuthenticationToken(principal, credentials, authorities)
