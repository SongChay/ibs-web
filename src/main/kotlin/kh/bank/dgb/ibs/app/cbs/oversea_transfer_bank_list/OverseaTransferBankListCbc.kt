package kh.bank.dgb.ibs.app.cbs.oversea_transfer_bank_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class OverseaTransferBankListRequest(
	val countryCode: String? = null,
	val channelTypeCode: String? = null,
)

data class OverseaTransferBankItem(
	val bankName: String? = null,
)

data class OverseaTransferBankListResponse(
	val grid01: List<OverseaTransferBankItem>? = null,
)

/** Port of `TRS4003_Adapter_InquiryBankList` — calls CBS opcode `CIB11300311` (the old
 *  `DGBEBankingService.processCIB11300311`). */
@RestController
@RequestMapping("/TRS4003")
class OverseaTransferBankListCbc(
	private val sbc: OverseaTransferBankListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<OverseaTransferBankListRequest>): ResponseData<OverseaTransferBankListResponse> =
		sbc.inquire(request)
}
