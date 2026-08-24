package kh.bank.dgb.ibs.app.cbs.save_sub_user

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SaveSubUserRequest(
	val userID: String? = null,
	val userName: String? = null,
	val phoneNumber: String? = null,
	val department: String? = null,
	val position: String? = null,
	val remark: String? = null,
	val email: String? = null,
	val loginUserID: String? = null,
	val channelTypeCode: String? = null,
	val password: String? = null,
	// Old Vo: getter serialized to CBS as "grid02", setter bound from the client as
	// "userAccountAccessInfoList" — kept as the CBS wire name here.
	@JsonProperty("grid02")
	val userAccountAccessInfoList: List<SaveSubUserAccountAccessItem>? = null,
	// Old Vo: getter serialized to CBS as "grid01", setter bound from the client as
	// "userMenuAccessInfoList" — kept as the CBS wire name here.
	@JsonProperty("grid01")
	val userMenuAccessInfoList: List<SaveSubUserMenuAccessItem>? = null,
)

data class SaveSubUserAccountAccessItem(
	val accountNo: String? = null,
	val accountFullAccessRightTypeCode: String? = null,
	val accountInquiryAccessRightTypeCode: String? = null,
	val accountAccessRightTypeCode: String? = null,
)

data class SaveSubUserMenuAccessItem(
	val level1MenuCode: String? = null,
	val level2MenuCode: String? = null,
	val menuUseRightTypeCode: String? = null,
)

data class SaveSubUserResponse(
	val resultYn: String? = null,
)

/** Port of `INF2104_Adapter_SaveSubUser` — NOT a single pass-through call. The old adapter:
 *   1. derives each account's `accountAccessRightTypeCode` from the full/inquiry access codes the
 *      client sent.
 *   2. calls CBS opcode `CIB11002411` (`processUSR0022`, "check duplicated user ID") first.
 *   3. branches on THAT result to call either `CIB11302421` (`processMGR0012`, add) or
 *      `CIB11302431` (`processMGR0013`, update).
 *  Full logic replicated in `SaveSubUserSbc` — see its doc comment for the exact (and
 *  counter-intuitive) branch condition. Flagged prominently for extra scrutiny per the task
 *  instructions: this is the one adapter in this batch with genuine cross-call business logic. */
@RestController
@RequestMapping("/INF2104")
class SaveSubUserCbc(
	private val sbc: SaveSubUserSbc,
) {
	@PostMapping
	fun save(@RequestBody request: RequestData<SaveSubUserRequest>): ResponseData<SaveSubUserResponse> =
		sbc.save(request)
}
