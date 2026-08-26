package kh.bank.dgb.ibs.app.cbs.recent_transfer_account_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of `TRS1201_Adapter_RecentTransferAccountList#process`. Beyond the plain CBS pass-through,
 * the old adapter copies `opponentBankName` into `opponentBankShortName` for every item in the
 * result list before returning it — replicated in [inquire] below.
 */
@Service
class RecentTransferAccountListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<RecentTransferAccountListRequest>): ResponseData<RecentTransferAccountListResponse> {
		val response = coreBankingApiConnector.post(
			"CIB11301111",
			request.header?.languageCode,
			request.body,
			RecentTransferAccountListResponse::class.java,
		)
		val enrichedList = response.body?.recentTransferFriendList?.map { it.copy(opponentBankShortName = it.opponentBankName) }
		return response.copy(body = response.body?.copy(recentTransferFriendList = enrichedList))
	}
}
