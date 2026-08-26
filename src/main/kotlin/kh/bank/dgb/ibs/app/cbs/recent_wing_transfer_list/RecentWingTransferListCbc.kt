package kh.bank.dgb.ibs.app.cbs.recent_wing_transfer_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RecentWingTransferListRequest(
	val userID: String? = null,
	val countMonth: Int? = null,
	val maxCount: Int? = null,
)

data class RecentWingTransferListItem(
	val transactionTypeCode: String? = null,
	val wingAccount: String? = null,
	val receiverName: String? = null,
)

data class RecentWingTransferListResponse(
	@JsonAlias("grid01") val recentWingTransferList: List<RecentWingTransferListItem>? = null,
)

/** Port of `TRS1202_Adapter_GetRecentWingTransfer` — calls CBS opcode `CIB11301813` (via the old
 *  `DGBEBankingService.processCIB11301813`). */
@RestController
@RequestMapping("/TRS1202")
class RecentWingTransferListCbc(
	private val recentWingTransferListSbc: RecentWingTransferListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RecentWingTransferListRequest>): ResponseData<RecentWingTransferListResponse> {
		return recentWingTransferListSbc.inquire(request)
	}
}
