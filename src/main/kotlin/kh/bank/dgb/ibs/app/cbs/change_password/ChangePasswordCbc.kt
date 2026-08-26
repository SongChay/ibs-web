package kh.bank.dgb.ibs.app.cbs.change_password

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ChangePasswordRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val currentUserPw: String? = null,
	val newUserPw: String? = null,
	val channelTypeCode: String? = null,
) {
	override fun toString(): String {
		return "ChangePasswordRequest(customerNo=$customerNo, userID=$userID, currentUserPw=********, newUserPw=********, channelTypeCode=$channelTypeCode)"
	}
}

data class ChangePasswordResponse(
	val resultYn: String? = null,
)

/** Port of `USR2101_Adapter_ChangePassword` — calls CBS opcode `CIB11000331`. */
@RestController
@RequestMapping("/USR2101")
class ChangePasswordCbc(
	private val changePasswordSbc: ChangePasswordSbc,
) {
	@PostMapping
	fun change(@RequestBody request: RequestData<ChangePasswordRequest>): ResponseData<ChangePasswordResponse> {
		return changePasswordSbc.change(request)
	}
}
