package kh.bank.dgb.ibs.app.cbs.transfer

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class TransferSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun transfer(request: RequestData<TransferRequest>): ResponseData<TransferResponse> {
		val body = request.body

		// Immediate (single) transfer doesn't need a schedule — port of the old adapter's
		// `if (transferTypeCode.equalsIgnoreCase("0001")) { scheduleDate = null; scheduleTime = null }`.
		val adjustedBody = if (body != null && body.transferTypeCode.equals("0001", ignoreCase = true)) {
			body.copy(scheduleDate = null, scheduleTime = null)
		} else {
			body
		}

		return coreBankingApiConnector.post("CIB11001021", request.header?.languageCode, adjustedBody, TransferResponse::class.java)
	}
}
