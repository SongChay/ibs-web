package kh.bank.dgb.ibs.app.cbs.oversea_transfer_country_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class OverseaTransferCountryListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<OverseaTransferCountryListRequest>): ResponseData<OverseaTransferCountryListResponse> {
		return coreBankingApiConnector.post("CIB11000303", request.header?.languageCode, request.body, OverseaTransferCountryListResponse::class.java)
	}
}
