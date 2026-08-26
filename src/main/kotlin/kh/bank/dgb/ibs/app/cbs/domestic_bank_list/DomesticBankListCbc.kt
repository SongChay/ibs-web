package kh.bank.dgb.ibs.app.cbs.domestic_bank_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DomesticBankListRequest(
	val userID: String? = null,
	val eBankTransactionTypeCode: String? = null,
)

data class DomesticBankItem(
	val description: String? = null,
	val rftYN: String? = null,
	val fastYN: String? = null,
	val usdOnlyYN: String? = null,
	val groupCode: String? = null,
	val code: String? = null,
	val value: String? = null,
	val rftBankCode: String? = null,
	val bakongYN: String? = null,
	val bakongShortName: String? = null,
	val bankLogoUrl: String? = null,
)

data class DomesticBankListResponse(
	val items: List<DomesticBankItem>? = null,
)

/** Port of `TRS6001_Adapter_RetrieveListBank` — calls CBS opcode `CIB11000013` (the old
 *  `DGBEBankingService.processCIB11000013`). Named "domestic bank list" (rather than reusing
 *  `oversea_transfer_bank_list`'s name) to keep it distinct from `TRS4003`'s oversea-transfer bank
 *  lookup: this one returns local payment-rail flags (RFT/FAST/Bakong), used for domestic/local
 *  transfer bank selection. */
@RestController
@RequestMapping("/TRS6001")
class DomesticBankListCbc(
	private val domesticBankListSbc: DomesticBankListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<DomesticBankListRequest>): ResponseData<DomesticBankListResponse> {
		return domesticBankListSbc.inquire(request)
	}
}
