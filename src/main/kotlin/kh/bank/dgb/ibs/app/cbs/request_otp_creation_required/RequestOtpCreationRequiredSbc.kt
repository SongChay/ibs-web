package kh.bank.dgb.ibs.app.cbs.request_otp_creation_required

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `USR0103_RES_OTPCreateRequiredVo` — the CBS wire shape. Old Vo read this field back
 *  from CBS under the oddly-cased key `oTPCreateRequiredYN` (`@JsonSetter`) but re-exposed it to
 *  the client under `oTPCreateRequiredYn` (`@JsonGetter`, lowercase `n`) — a real asymmetry, not a
 *  typo we can collapse. Kept as two distinct shapes rather than one field with conflicting
 *  annotations. */
data class OtpCreationRequiredCbsResponse(
	val oTPCreateRequiredYN: String? = null,
)

@Service
class RequestOtpCreationRequiredSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RequestOtpCreationRequiredRequest>): ResponseData<RequestOtpCreationRequiredResponse> {
		val cbsResult = coreBankingApiConnector.post(
			"CIB11000214",
			request.header?.languageCode,
			request.body,
			OtpCreationRequiredCbsResponse::class.java,
		)
		return ResponseData(
			header = cbsResult.header,
			body = cbsResult.body?.let { RequestOtpCreationRequiredResponse(otpCreateRequiredYn = it.oTPCreateRequiredYN) },
		)
	}
}
