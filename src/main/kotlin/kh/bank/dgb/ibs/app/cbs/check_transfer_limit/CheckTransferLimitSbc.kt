package kh.bank.dgb.ibs.app.cbs.check_transfer_limit

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CheckTransferLimitSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun check(request: RequestData<CheckTransferLimitRequest>): ResponseData<CheckTransferLimitResponse> =
		connector.post("CIB11812312", request.header?.languageCode, request.body, CheckTransferLimitResponse::class.java)
}
