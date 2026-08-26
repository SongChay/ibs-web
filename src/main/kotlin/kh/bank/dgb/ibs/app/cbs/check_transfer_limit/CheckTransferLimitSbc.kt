package kh.bank.dgb.ibs.app.cbs.check_transfer_limit

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CheckTransferLimitSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun check(request: RequestData<CheckTransferLimitRequest>): ResponseData<CheckTransferLimitResponse> {
		return coreBankingApiConnector.post("CIB11812312", request.header?.languageCode, request.body, CheckTransferLimitResponse::class.java)
	}
}
