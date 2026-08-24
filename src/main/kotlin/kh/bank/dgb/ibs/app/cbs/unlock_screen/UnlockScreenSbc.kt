package kh.bank.dgb.ibs.app.cbs.unlock_screen

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class UnlockScreenSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun unlock(request: RequestData<UnlockScreenRequest>): ResponseData<UnlockScreenResponse> {
		val result = connector.post("CIB11300291", request.header?.languageCode, request.body, UnlockScreenResponse::class.java)
		val body = result.body ?: UnlockScreenResponse()

		return if (result.header?.result == true) {
			result.copy(body = body.copy(resultYN = "Y"))
		} else {
			// Port of the old `StrSubstitutor` templating: replace "${maxPasswordErrorCount}" /
			// "${passwordErrorCount}" placeholders in the header message with the actual counts.
			val substitutedMessage = result.header?.resultMessage
				?.replace("\${maxPasswordErrorCount}", body.maxPasswordErrorCount?.toString().orEmpty())
				?.replace("\${passwordErrorCount}", body.passwordErrorCount?.toString().orEmpty())

			result.copy(
				header = result.header?.copy(resultMessage = substitutedMessage),
				body = body.copy(resultYN = "N"),
			)
		}
	}
}
