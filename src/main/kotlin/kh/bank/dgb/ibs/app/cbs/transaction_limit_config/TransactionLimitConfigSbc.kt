package kh.bank.dgb.ibs.app.cbs.transaction_limit_config

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class TransactionLimitConfigSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<TransactionLimitConfigRequest>): ResponseData<TransactionLimitConfigResponse> =
		connector.post("CIB11812311", request.header?.languageCode, request.body, TransactionLimitConfigResponse::class.java)
}
