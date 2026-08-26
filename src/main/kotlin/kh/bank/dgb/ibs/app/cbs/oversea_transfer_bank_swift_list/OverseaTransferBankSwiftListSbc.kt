package kh.bank.dgb.ibs.app.cbs.oversea_transfer_bank_swift_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class OverseaTransferBankSwiftListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<OverseaTransferBankSwiftListRequest>): ResponseData<OverseaTransferBankSwiftListResponse> {
		return coreBankingApiConnector.post("CIB11300811", request.header?.languageCode, request.body, OverseaTransferBankSwiftListResponse::class.java)
	}
}
