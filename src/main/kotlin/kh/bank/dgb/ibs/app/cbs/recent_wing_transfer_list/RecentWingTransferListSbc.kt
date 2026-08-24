package kh.bank.dgb.ibs.app.cbs.recent_wing_transfer_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RecentWingTransferListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RecentWingTransferListRequest>): ResponseData<RecentWingTransferListResponse> =
		connector.post("CIB11301813", request.header?.languageCode, request.body, RecentWingTransferListResponse::class.java)
}
