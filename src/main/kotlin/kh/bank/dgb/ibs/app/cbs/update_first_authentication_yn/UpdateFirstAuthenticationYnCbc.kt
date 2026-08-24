package kh.bank.dgb.ibs.app.cbs.update_first_authentication_yn

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UpdateFirstAuthenticationYnRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

data class UpdateFirstAuthenticationYnResponse(
	val resultYn: String? = null,
)

/** Port of `USR2301_Adapter_UpdateFirstAuthenticationYn` — calls CBS opcode `CIB11000332`. */
@RestController
@RequestMapping("/USR2301")
class UpdateFirstAuthenticationYnCbc(
	private val sbc: UpdateFirstAuthenticationYnSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<UpdateFirstAuthenticationYnRequest>): ResponseData<UpdateFirstAuthenticationYnResponse> =
		sbc.update(request)
}
