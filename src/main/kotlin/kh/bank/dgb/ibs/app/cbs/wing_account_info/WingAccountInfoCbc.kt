package kh.bank.dgb.ibs.app.cbs.wing_account_info

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class WingAccountInfoRequest(
	val wingAccount: String? = null,
)

data class WingAccountInfoResponse(
	val wingAccount: String? = null,
	val walletCurrency: String? = null,
	val accountName: String? = null,
)

/** Port of `TRS5002_Adapter_GetWingAccountInfo` — calls CBS opcode `CIB11001811` (the old
 *  `DGBEBankingService.processWNG002`). */
@RestController
@RequestMapping("/TRS5002")
class WingAccountInfoCbc(
	private val wingAccountInfoSbc: WingAccountInfoSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<WingAccountInfoRequest>): ResponseData<WingAccountInfoResponse> {
		return wingAccountInfoSbc.inquire(request)
	}
}
