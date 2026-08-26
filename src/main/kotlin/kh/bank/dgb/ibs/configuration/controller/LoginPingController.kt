package kh.bank.dgb.ibs.configuration.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Port of `SecurityController.login` (`POST /login`) — a deliberate no-op in the old app too
 * (empty method body, bare 200), yet still confirmed referenced by the real client's compiled JS.
 * Best-guess purpose: a reachability/pre-flight ping the login page hits before starting the
 * RSA/AES handshake — actual credential submission happens at `/security_check`, handled
 * separately by `JsonCredentialsAuthenticationFilter`. Replicated as the same no-op rather than
 * guessing at behavior the old app never actually had.
 */
@RestController
class LoginPingController {
	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	fun login() {
		return Unit
	}
}
