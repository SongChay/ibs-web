package kh.bank.dgb.ibs.app.cbs.wing_transfer_fee

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class WingTransferFeeSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<WingTransferFeeRequest>): ResponseData<WingTransferFeeResponse> =
		connector.post("CIB11001812", request.header?.languageCode, request.body, WingTransferFeeResponse::class.java)
}
