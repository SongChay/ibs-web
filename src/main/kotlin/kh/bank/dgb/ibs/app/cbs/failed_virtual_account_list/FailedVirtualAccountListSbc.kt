package kh.bank.dgb.ibs.app.cbs.failed_virtual_account_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `VAC1003_RES_WrapperViewFailedVirtualAccountListVo` — the CBS wire shape. `totalCount`
 *  reads back under `grid01Count` and the list under `grid01` (old Vo's `@JsonSetter`s), while the
 *  client-facing names are `totalCount`/`virtualAccountList` (old Vo's `@JsonGetter`s). Item
 *  fields have no such rename, so `FailedVirtualAccountListItem` is reused directly. */
data class FailedVirtualAccountListCbsResponse(
	val grid01Count: Int? = null,
	val grid01: List<FailedVirtualAccountListItem>? = null,
)

@Service
class FailedVirtualAccountListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<FailedVirtualAccountListRequest>): ResponseData<FailedVirtualAccountListResponse> {
		val cbsResult = coreBankingApiConnector.post(
			"CIB11002012",
			request.header?.languageCode,
			request.body,
			FailedVirtualAccountListCbsResponse::class.java,
		)

		return ResponseData(
			header = cbsResult.header,
			body = FailedVirtualAccountListResponse(
				totalCount = cbsResult.body?.grid01Count,
				virtualAccountList = cbsResult.body?.grid01,
			),
		)
	}
}
