package kh.bank.dgb.ibs.cbs.client

import kh.bank.dgb.ibs.common.envelope.RequestUserHeaderVo

data class ChannelRsaKeyResult(
	val modulus: String,
	val exponent: String,
)

/** Requests CBS's own RSA public key (see `CoreBankingRsaProperties`), used to encrypt the
 *  password before the ATH0001 login call. Returns `null` on any failure — the caller (
 *  `DefaultCoreBankingAuthClient`) turns that into a login failure, same as the old
 *  `AuthenticationProviderImpl` throwing out of its RSA sub-call. */
fun interface CoreBankingRsaClient {
	fun requestPublicKey(header: RequestUserHeaderVo): ChannelRsaKeyResult?
}
