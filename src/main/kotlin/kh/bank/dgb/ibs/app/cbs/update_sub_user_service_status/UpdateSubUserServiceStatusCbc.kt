package kh.bank.dgb.ibs.app.cbs.update_sub_user_service_status

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UpdateSubUserServiceStatusRequest(
	val masterUserID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val subUserIDList: List<UserIdItem>? = null,
	val serviceStatusCode: String? = null,
)

data class UserIdItem(
	val userID: String? = null,
)

data class UpdateSubUserServiceStatusResponse(
	val subUserIDList: List<UserIdItem>? = null,
)

/** Port of `INF2003_Adapter_UpdateServiceStatusSubUser` — calls CBS opcode `CIB11002332` (via the
 *  old `DGBEBankingService.processMGR0015`). */
@RestController
@RequestMapping("/INF2003")
class UpdateSubUserServiceStatusCbc(
	private val updateSubUserServiceStatusSbc: UpdateSubUserServiceStatusSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<UpdateSubUserServiceStatusRequest>): ResponseData<UpdateSubUserServiceStatusResponse> {
		return updateSubUserServiceStatusSbc.update(request)
	}
}
