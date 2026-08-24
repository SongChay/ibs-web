package kh.bank.dgb.ibs.app.cbs.wing_transfer_registration

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class WingTransferRegistrationSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun register(request: RequestData<WingTransferRegistrationRequest>): ResponseData<WingTransferRegistrationResponse> {
		val body = request.body ?: WingTransferRegistrationRequest()
		// Port of: `item.setReceiverCountryCode("KHM")` for every item in the transfer list.
		val withKhmCountryCode = body.copy(transferList = body.transferList?.map { it.copy(receiverCountryCode = "KHM") })
		return connector.post(OPCODE, request.header?.languageCode, withKhmCountryCode, WingTransferRegistrationResponse::class.java)
	}

	companion object {
		const val OPCODE = "CIB11001921"
	}
}
