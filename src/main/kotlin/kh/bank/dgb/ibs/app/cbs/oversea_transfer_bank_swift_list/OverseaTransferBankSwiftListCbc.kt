package kh.bank.dgb.ibs.app.cbs.oversea_transfer_bank_swift_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class OverseaTransferBankSwiftListRequest(
	val bankName: String? = null,
	val countryCode: String? = null,
)

data class OverseaTransferBankSwiftItem(
	val swiftCode: String? = null,
	val address: String? = null,
)

data class OverseaTransferBankSwiftListResponse(
	val grid01: List<OverseaTransferBankSwiftItem>? = null,
)

/** Port of `TRS4004_Adapter_InquiryBankSwiftList` — calls CBS opcode `CIB11300811` (the old
 *  `DGBEBankingService.processCIB11300811`). */
@RestController
@RequestMapping("/TRS4004")
class OverseaTransferBankSwiftListCbc(
	private val overseaTransferBankSwiftListSbc: OverseaTransferBankSwiftListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<OverseaTransferBankSwiftListRequest>): ResponseData<OverseaTransferBankSwiftListResponse> {
		return overseaTransferBankSwiftListSbc.inquire(request)
	}
}
