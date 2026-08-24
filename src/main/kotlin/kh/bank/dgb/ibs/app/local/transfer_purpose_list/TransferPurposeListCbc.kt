package kh.bank.dgb.ibs.app.local.transfer_purpose_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DocumentItem(
	val code: String,
	val item: String?,
)

data class TransferPurposeItem(
	val code: String,
	val item: String?,
	val item1: String?,
	val prepareDocument: List<DocumentItem>,
	val remarkDocument: List<DocumentItem>,
)

/**
 * Port of `TRS4105_Adapter_InquiryPurposeTransferList` — purely local, but not a 1:1 wrapper
 * around a single DAO: it composes `PurposeOfTransferRbc` (from `app/local/purpose_of_transfer`)
 * with `PrepareDocumentRbc` and `RemarkDocumentRbc` (from their own folders) to build one
 * enriched list. That composition is the actual "feature" here, so it gets its own folder with
 * no `Rbc` of its own — the `Sbc` reaches into the three existing ones directly.
 *
 * Single old adapter in this file, so the class-level `@RequestMapping` is kept (not removed) and
 * pointed directly at that adapter's absolute route.
 */
@RestController
@RequestMapping("/TRS4105")
class TransferPurposeListCbc(
	private val transferPurposeListSbc: TransferPurposeListSbc,
) {

	@PostMapping
	fun list(@RequestBody request: RequestData<Unit>): ResponseData<List<TransferPurposeItem>> {
		val result = transferPurposeListSbc.getList()
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
