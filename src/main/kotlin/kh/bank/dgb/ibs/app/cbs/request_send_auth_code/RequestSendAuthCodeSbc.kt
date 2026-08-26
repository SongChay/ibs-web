package kh.bank.dgb.ibs.app.cbs.request_send_auth_code

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of the commented-out `USR2003_Adapter_RequestSendAuthCode`. Old code forced
 * `channelTypeCode = "01"` and a hardcoded `serviceID = "10002100034"` ("Corporate User
 * Authentication Regist", per its sibling USR2004's comment) onto the request body before calling
 * CBS, overriding anything the client sent for those two fields — replicated below.
 */
@Service
class RequestSendAuthCodeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun request(request: RequestData<RequestSendAuthCodeRequest>): ResponseData<RequestSendAuthCodeResponse> {
		val forcedBody = (request.body ?: RequestSendAuthCodeRequest())
			.copy(channelTypeCode = "01", serviceID = "10002100034")

		return coreBankingApiConnector.post("CIB11000212", request.header?.languageCode, forcedBody, RequestSendAuthCodeResponse::class.java)
	}
}
