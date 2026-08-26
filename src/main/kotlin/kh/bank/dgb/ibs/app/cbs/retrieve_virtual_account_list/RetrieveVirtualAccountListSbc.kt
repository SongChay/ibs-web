package kh.bank.dgb.ibs.app.cbs.retrieve_virtual_account_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `VAC1007_RES_WrapperRetrieveVirtualAccountListVo` — the CBS wire shape. List reads
 *  back from CBS under `grid01` (old Vo's `@JsonSetter`) but is exposed to the client under
 *  `virtualAccountList` (old Vo's `@JsonGetter`). */
data class RetrieveVirtualAccountListCbsResponse(
	val grid01: List<RetrieveVirtualAccountItem>? = null,
)

@Service
class RetrieveVirtualAccountListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun retrieve(request: RequestData<RetrieveVirtualAccountListRequest>): ResponseData<RetrieveVirtualAccountListResponse> {
		val cbsResult = coreBankingApiConnector.post(
			"CIB11302211",
			request.header?.languageCode,
			request.body,
			RetrieveVirtualAccountListCbsResponse::class.java,
		)

		return ResponseData(
			header = cbsResult.header,
			body = RetrieveVirtualAccountListResponse(virtualAccountList = cbsResult.body?.grid01),
		)
	}
}
