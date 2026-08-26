package kh.bank.dgb.ibs.app.cbs.recent_oversea_transfer_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class RecentOverseaTransferListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RecentOverseaTransferListRequest>): ResponseData<RecentOverseaTransferListResponse> {
		return coreBankingApiConnector.post("CIB11301612", request.header?.languageCode, request.body, RecentOverseaTransferListResponse::class.java)
	}
}
