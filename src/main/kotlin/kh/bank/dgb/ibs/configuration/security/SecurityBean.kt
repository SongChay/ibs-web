package kh.bank.dgb.ibs.configuration.security

import kh.bank.dgb.ibs.configuration.filter.authentication.JsonCredentialsAuthenticationFilter
import kh.bank.dgb.ibs.configuration.security.handler.CustomAuthenticationEntryPoint
import kh.bank.dgb.ibs.configuration.security.handler.CustomAuthenticationFailureHandler
import kh.bank.dgb.ibs.configuration.security.handler.CustomAuthenticationSuccessHandler
import kh.bank.dgb.ibs.configuration.security.handler.CustomLogoutSuccessHandler
import kh.bank.dgb.ibs.configuration.security.handler.ExpiredSessionStrategyHandler
import kh.bank.dgb.ibs.configuration.security.handler.InvalidSessionStrategyHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.session.HttpSessionEventPublisher
import tools.jackson.databind.ObjectMapper

/**
 * Port of `spring-security.xml`: URL authorization rules, CSRF, logout, session-fixation
 * protection, single-session concurrency control, and the login mechanics — including the
 * core-banking credential check (`IbsAuthenticationProvider` + `CoreBankingAuthClient`, per the
 * confirmed simplified true/false-only CBS contract) and JSON-body credential parsing
 * (`JsonCredentialsAuthenticationFilter`).
 *
 * CSRF stays disabled, matching the old config exactly — the existing client doesn't send a CSRF
 * token, so enabling it now would break every write request. (The old XML also declared an
 * `HttpSessionCsrfTokenRepository` bean despite CSRF being off; that was dead/half-wired
 * configuration and isn't carried over.)
 *
 * Renamed from `SecurityConfig` to match this project's convention: a `@Configuration` class whose
 * job is producing `@Bean`s gets a `Bean` suffix, not a `Config` one.
 */
@Configuration
class SecurityBean(
	private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
	private val invalidSessionStrategyHandler: InvalidSessionStrategyHandler,
	private val expiredSessionStrategyHandler: ExpiredSessionStrategyHandler,
	private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler,
	private val customAuthenticationFailureHandler: CustomAuthenticationFailureHandler,
	private val customLogoutSuccessHandler: CustomLogoutSuccessHandler,
	private val objectMapper: ObjectMapper,
) {

	@Bean
	fun securityFilterChain(http: HttpSecurity, authenticationConfiguration: AuthenticationConfiguration): SecurityFilterChain {
		val authenticationManager: AuthenticationManager = authenticationConfiguration.authenticationManager

		val loginFilter = JsonCredentialsAuthenticationFilter(authenticationManager, objectMapper).apply {
			setFilterProcessesUrl("/security_check")
			setAuthenticationSuccessHandler(customAuthenticationSuccessHandler)
			setAuthenticationFailureHandler(customAuthenticationFailureHandler)
		}

		http {
			authorizeHttpRequests {
				authorize("/login", permitAll)
				authorize("/security_check", permitAll)
				authorize("/session_exp", permitAll)
				authorize("/RSA", permitAll)
				authorize("/AES", permitAll)
				authorize("/api/images/resources/**", permitAll)
				authorize("/download/**", permitAll)
				authorize("/", permitAll)
				authorize("/getCurrentHHMM", permitAll)
				authorize("/generateQrCode/**", permitAll)
				authorize("/upload/companyProfile", permitAll)
				authorize("/upload/docfile", permitAll)
				authorize(anyRequest, authenticated)
			}
			addFilterAt<UsernamePasswordAuthenticationFilter>(loginFilter)
			logout {
				logoutUrl = "/signout"
				deleteCookies("JSESSIONID")
				invalidateHttpSession = true
				logoutSuccessHandler = customLogoutSuccessHandler
			}
			csrf { disable() }
			sessionManagement {
				sessionFixation { migrateSession() }
				invalidSessionStrategy = invalidSessionStrategyHandler
				sessionConcurrency {
					maximumSessions = 1
					expiredSessionStrategy = expiredSessionStrategyHandler
				}
			}
			exceptionHandling {
				authenticationEntryPoint = customAuthenticationEntryPoint
			}
		}
		return http.build()
	}

	/** Lets the concurrency-control session registry hear about session destruction — same role
	 *  as the old `HttpSessionEventPublisher` listener declared in web.xml. */
	@Bean
	fun httpSessionEventPublisher(): HttpSessionEventPublisher {
		return HttpSessionEventPublisher()
	}
}
