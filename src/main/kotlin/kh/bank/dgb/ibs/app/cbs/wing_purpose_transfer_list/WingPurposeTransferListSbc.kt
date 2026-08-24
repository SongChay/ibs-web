package kh.bank.dgb.ibs.app.cbs.wing_purpose_transfer_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class WingPurposeTransferListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<WingPurposeTransferListRequest>): ResponseData<WingPurposeTransferListResponse> {
		val result = connector.post(OPCODE, request.header?.languageCode, request.body, WingPurposeTransferListResponse::class.java)

		// Port of: "In case get error from external server we will set true to header result"
		val header = result.header
		if (header != null && header.result == false) {
			return result.copy(header = header.copy(result = true))
		}
		return result
	}

	companion object {
		private const val OPCODE = "CIB11001801"
	}
}
