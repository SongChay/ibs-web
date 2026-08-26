package kh.bank.dgb.ibs.app.cbs.change_password

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `USR2101_Adapter_ChangePassword`. Old adapter set `resultYn = "Y"` on the response body
 *  whenever the CBS call both returned a body and succeeded — replicated below. */
@Service
class ChangePasswordSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun change(request: RequestData<ChangePasswordRequest>): ResponseData<ChangePasswordResponse> {
		val cbsResult = coreBankingApiConnector.post("CIB11000331", request.header?.languageCode, request.body, ChangePasswordResponse::class.java)

		val body = if (cbsResult.header?.result == true && cbsResult.body != null) {
			cbsResult.body.copy(resultYn = "Y")
		} else {
			cbsResult.body
		}

		return ResponseData(header = cbsResult.header, body = body)
	}
}
