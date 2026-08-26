package kh.bank.dgb.ibs.app.cbs.transfer

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class TransferListItem(
	val amount: BigDecimal? = null,
	val toAccountNumber: String? = null,
	val bankCode: String? = null,
	val recipientName: String? = null,
	val receiverAccountRemark: String? = null,
	val transactionTypeCode: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class ApprovalLineItem(
	val approverTypeCode: String? = null,
	val approverID: String? = null,
)

/** The old `TRS1102_REQ_RegisterTransferVo` read `transferList`/`approvalList` in from its own
 *  client under those names but sent them out to CBS under `grid01`/`grid02`. A bare
 *  `@get:JsonProperty` alone does NOT preserve that asymmetry — empirically verified (Jackson 3 +
 *  jackson-module-kotlin 3.1.5) that it renames BOTH directions, so deserializing a real client
 *  request under the default `transferList`/`approvalList` keys would silently bind `null`. Needs
 *  the explicit `@param:JsonProperty` alongside it to pin deserialization to the client-facing name
 *  while `@get:` alone governs the CBS-facing serialized name. */
data class TransferRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val memo: String? = null,
	val scheduleDate: String? = null,
	val scheduleTime: String? = null,
	val previousApprovalNo: Long? = null,
	@param:JsonProperty("transferList") @get:JsonProperty("grid01")
	val transferList: List<TransferListItem>? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid02")
	val approvalList: List<ApprovalLineItem>? = null,
)

data class AccountTransferResultItem(
	val cancelTransactionYN: String? = null,
	val errorMsgContent: String? = null,
)

/** The old `TRS1102_RES_TransferVo` used a single `@JsonProperty("grid02")` (not a split
 *  getter/setter pair) for `accountTransferResult`, meaning CBS's wire key and the old app's own
 *  outward key were both literally `grid02` — replicated symmetrically here with a plain
 *  `@JsonProperty`, unlike the split alias used for the other CBS "grid0N" wrapper fields. */
data class TransferResponse(
	val approvalNo: Long? = null,
	val resultYn: String? = null,
	@JsonProperty("grid02")
	val accountTransferResult: List<AccountTransferResultItem>? = null,
)

/** Port of `TRS1102_Adapter_Transfer` — calls CBS opcode `CIB11001021`
 *  (via the old `DGBEBankingService.processAPR0001`).
 *
 *  NOT a plain pass-through: for an immediate (single) transfer (`transferTypeCode == "0001"`) the
 *  old adapter clears `scheduleDate`/`scheduleTime` before calling CBS, since immediate transfers
 *  don't carry a schedule. Replicated in the Sbc. This is the core money-movement endpoint in this
 *  batch — flagged for extra review. */
@RestController
@RequestMapping("/TRS1102")
class TransferCbc(
	private val transferSbc: TransferSbc,
) {
	@PostMapping
	fun transfer(@RequestBody request: RequestData<TransferRequest>): ResponseData<TransferResponse> {
		return transferSbc.transfer(request)
	}
}
