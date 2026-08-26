package kh.bank.dgb.ibs.cbs.client

import kh.bank.dgb.ibs.cbs.model.Ath0001Request
import kh.bank.dgb.ibs.cbs.model.Ath0001Response
import kh.bank.dgb.ibs.cbs.model.Ath0001Result
import kh.bank.dgb.ibs.common.envelope.RequestUserHeaderVo
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.configuration.security.handshake.rsa.ChannelRsaUtils
import org.springframework.stereotype.Component

/**
 * Port of the CBS half of `AuthenticationProviderImpl.authenticate` (the RSA-handshake +
 * `processATH0001` half; the Spring-Security-token-building half lives in
 * `IbsAuthenticationProvider`, matching this app's split between "talk to CBS" (`cbs` package) and
 * "own the Authentication" (`configuration.security` package) that every other feature already
 * follows).
 *
 * Message-substitution of `resultMessage` (old app's `StrSubstitutor` over
 * `${passwordErrorCount}`/`${maxPasswordErrorCount}`) happens here, right where the profile with
 * those counts is available — same place the old code did it.
 *
 * UNVERIFIED against a live CBS, like every other CBS integration in this app.
 */
@Component
class DefaultCoreBankingAuthClient(
	private val rsaClient: CoreBankingRsaClient,
	private val coreBankingApiConnector: CoreBankingApiConnector,
) : CoreBankingAuthClient {

	override fun authenticate(userId: String, password: String, channelTypeCode: String, languageCode: String?): Ath0001Result {
		val header = RequestUserHeaderVo(userID = userId, channelTypeCode = channelTypeCode, languageCode = languageCode)

		val publicKey = rsaClient.requestPublicKey(header)
			?: return Ath0001Result.Failure(
				ResponseResultCodeType.CBK_NO_RESPONSE_HEADER_EB.value,
				ResponseResultCodeType.CBK_NO_RESPONSE_HEADER_EB.description,
			)

		val encryptedPassword = ChannelRsaUtils.encrypt(password, publicKey.modulus, publicKey.exponent)
		val requestBody = Ath0001Request(userID = userId, userPwd = encryptedPassword, channelTypeCode = channelTypeCode)

		val response = coreBankingApiConnector.post(OPCODE_LOGIN, languageCode, requestBody, Ath0001Response::class.java)
		val profile = response.body

		return if (response.header?.result == true && profile != null) {
			Ath0001Result.Success(header = response.header, profile = profile)
		} else {
			val resultMessage = response.header?.resultMessage
				?.replace("\${maxPasswordErrorCount}", (profile?.maxPasswordErrorCount ?: 0).toString())
				?.replace("\${passwordErrorCount}", (profile?.passwordErrorCount ?: 0).toString())
			Ath0001Result.Failure(response.header?.resultCode, resultMessage)
		}
	}

	companion object {
		/** Port of `DGBEBankingServiceImpl.processATH0001`'s opcode. */
		private const val OPCODE_LOGIN = "CIB11300191"
	}
}
