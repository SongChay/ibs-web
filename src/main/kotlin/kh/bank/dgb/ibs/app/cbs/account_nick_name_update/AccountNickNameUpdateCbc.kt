package kh.bank.dgb.ibs.app.cbs.account_nick_name_update

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AccountNickNameUpdateRequest(
	val masterUserID: String? = null,
	val accountNo: String? = null,
	val accountNickName: String? = null,
)

/** Port of `ACI1007_RES_UpdateAccountNickNameVo` — empty body in the old app. */
class AccountNickNameUpdateResponse

/** Port of `ACI1007_Adapter_UpdateAccountNickName` — calls CBS opcode `CIB11000631` (via the old
 *  `DGBEBankingService.processACC0014`). Plain pass-through. */
@RestController
@RequestMapping("/ACI1007")
class AccountNickNameUpdateCbc(
	private val sbc: AccountNickNameUpdateSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<AccountNickNameUpdateRequest>): ResponseData<AccountNickNameUpdateResponse> =
		sbc.update(request)
}
