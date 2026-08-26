package kh.bank.dgb.ibs.cbs.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Port of the old `PropertiesPlaceholderConfiguration.rSAUrl` (`${rsa.server.address}`) — CBS's
 * OWN RSA key-exchange endpoint, used internally by this app's login flow to RSA-encrypt the
 * password before calling CBS's ATH0001 login opcode. Entirely separate from this app's
 * client-facing `/rsa`+`/aes` handshake (`configuration.controller.RsaHandshakeController`) —
 * that one protects the browser<->this-app channel; this one protects the this-app<->CBS channel,
 * uses a different RSA padding scheme's key material (modulus/exponent, not an X.509 blob — see
 * `ChannelRsaUtils`), and a flat (non-Maru-envelope) response shape (see `CoreBankingRsaClient`).
 *
 * No real value known for `url` (never in this repo, per the old app's per-environment file-swap
 * convention) — placeholder default in the same style as `CoreBankingProperties.baseUrl`, override
 * via `IBS_CBS_RSA_URL` in any real environment.
 */
@Component
@ConfigurationProperties(prefix = "ibs.cbs.rsa")
data class CoreBankingRsaProperties(
	var url: String = "http://127.0.0.1:10210/ONLWeb/HttpCsbRsaAdapter",
	var terminalUniqueNo: String = "1",
)
