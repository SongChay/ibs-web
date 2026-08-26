package kh.bank.dgb.ibs.app.cbs.check_transfer_fee

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CheckTransferFeeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun check(request: RequestData<CheckTransferFeeRequest>): ResponseData<CheckTransferFeeResponse> {
		return coreBankingApiConnector.post("CIB11000813", request.header?.languageCode, request.body, CheckTransferFeeResponse::class.java)
	}
}
