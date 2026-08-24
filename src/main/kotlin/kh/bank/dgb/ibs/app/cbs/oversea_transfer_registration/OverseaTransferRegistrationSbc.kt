package kh.bank.dgb.ibs.app.cbs.oversea_transfer_registration

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class OverseaTransferRegistrationSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun register(request: RequestData<OverseaTransferRegistrationRequest>): ResponseData<OverseaTransferRegistrationResponse> =
		connector.post(OPCODE, request.header?.languageCode, request.body, OverseaTransferRegistrationResponse::class.java)

	companion object {
		const val OPCODE = "CIB11301721"
	}
}
