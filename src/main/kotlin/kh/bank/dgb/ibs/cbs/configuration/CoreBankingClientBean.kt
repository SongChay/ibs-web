package kh.bank.dgb.ibs.cbs.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

/**
 * Port of `spring-service.xml`'s `restTemplate`/`clientHttpRequestFactory` beans — same timeouts
 * (630s read, 30s connect) carried over verbatim from the old app, since core-banking calls were
 * apparently observed to need that much headroom.
 *
 * Renamed from `CoreBankingClientConfig` to match this project's convention: a `@Configuration`
 * class whose job is producing `@Bean`s gets a `Bean` suffix, not a `Config` one.
 */
@Configuration
class CoreBankingClientBean {

	@Bean
	fun coreBankingRestClient(): RestClient {
		val requestFactory = SimpleClientHttpRequestFactory().apply {
			setConnectTimeout(Duration.ofSeconds(30))
			setReadTimeout(Duration.ofSeconds(630))
		}
		return RestClient.builder().requestFactory(requestFactory).build()
	}
}
