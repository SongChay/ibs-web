package kh.bank.dgb.ibs.app.cbs.verify_authentication_code

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of the commented-out `USR2004_Adapter_VerifyAuthenticationCode`. Old code forced
 * `channelTypeCode = "01"` and a hardcoded `serviceID = "10002100034"` ("Corporate User
 * Authentication Regist") onto the request body before calling CBS, overriding anything the
 * client sent for those two fields — replicated below.
 */
@Service
class VerifyAuthenticationCodeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun verify(request: RequestData<VerifyAuthenticationCodeRequest>): ResponseData<VerifyAuthenticationCodeResponse> {
		val forcedBody = (request.body ?: VerifyAuthenticationCodeRequest())
			.copy(channelTypeCode = "01", serviceID = "10002100034")

		return coreBankingApiConnector.post("CIB11000213", request.header?.languageCode, forcedBody, VerifyAuthenticationCodeResponse::class.java)
	}
}
