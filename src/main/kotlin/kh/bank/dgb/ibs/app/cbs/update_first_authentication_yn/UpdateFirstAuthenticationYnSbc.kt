package kh.bank.dgb.ibs.app.cbs.update_first_authentication_yn

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `USR2301_Adapter_UpdateFirstAuthenticationYn`. Old adapter set `resultYn = "Y"` on the
 *  response body whenever the CBS call both returned a body and succeeded — replicated below. */
@Service
class UpdateFirstAuthenticationYnSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<UpdateFirstAuthenticationYnRequest>): ResponseData<UpdateFirstAuthenticationYnResponse> {
		val cbsResult = connector.post("CIB11000332", request.header?.languageCode, request.body, UpdateFirstAuthenticationYnResponse::class.java)

		val body = if (cbsResult.header?.result == true && cbsResult.body != null) {
			cbsResult.body.copy(resultYn = "Y")
		} else {
			cbsResult.body
		}

		return ResponseData(header = cbsResult.header, body = body)
	}
}
