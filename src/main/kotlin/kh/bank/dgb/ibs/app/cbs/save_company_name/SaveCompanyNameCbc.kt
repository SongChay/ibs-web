package kh.bank.dgb.ibs.app.cbs.save_company_name

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SaveCompanyNameRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val corporateName: String? = null,
)

/** Port of `ADS1002_RES_SaveCompanyNameVo` — empty body in the old app. */
class SaveCompanyNameResponse

/** Port of `ADS1002_Adapter_SaveCompanyName` — calls CBS opcode `CIB11002931` (via the old
 *  `DGBEBankingService.processUSR0101`). Plain pass-through. */
@RestController
@RequestMapping("/ADS1002")
class SaveCompanyNameCbc(
	private val sbc: SaveCompanyNameSbc,
) {
	@PostMapping
	fun save(@RequestBody request: RequestData<SaveCompanyNameRequest>): ResponseData<SaveCompanyNameResponse> =
		sbc.save(request)
}
