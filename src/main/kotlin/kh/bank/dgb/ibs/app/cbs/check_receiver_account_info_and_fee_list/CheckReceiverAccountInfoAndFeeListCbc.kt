package kh.bank.dgb.ibs.app.cbs.check_receiver_account_info_and_fee_list

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class CheckReceiverAccountInfoAndFeeRequestItem(
	val indexNo: Int? = null,
	val accountNo: String? = null,
	val bankCode: String? = null,
	val amount: BigDecimal? = null,
	val feeTypeCode: String? = null,
)

/** The old `TRS1006_REQ_WrapperCheckReceiverAccountInfoAndFeeVo` read this list in from its own
 *  client under `accountList` but sent it out to CBS under `grid01` — `@get:JsonProperty` here
 *  only overrides the outbound (CBS-facing) serialization, leaving inbound deserialization (from
 *  our own client) on the default `accountList` key. */
data class CheckReceiverAccountInfoAndFeeListRequest(
	val customerNo: String? = null,
	val accountNo: String? = null,
	val currencyCode: String? = null,
	@get:JsonProperty("grid01")
	val accountList: List<CheckReceiverAccountInfoAndFeeRequestItem>? = null,
)

data class CheckReceiverAccountInfoAndFeeResponseItem(
	val indexNo: Int? = null,
	val accountNo: String? = null,
	val bankCode: String? = null,
	val receiverName: String? = null,
	val currencyCode: String? = null,
	val validAccountYn: String? = null,
	val validAccountYnDesc: String? = null,
	val transferFee: BigDecimal? = null,
	val feeCurrencyCode: String? = null,
	val feeResultYn: String? = null,
	val feeResultYnDesc: String? = null,
)

data class CheckReceiverAccountInfoAndFeeListResponse(
	@param:JsonAlias("grid01")
	val accountList: List<CheckReceiverAccountInfoAndFeeResponseItem>? = null,
)

/** Port of `TRS1006_Adapter_InquiryCheckReceiverAccountInfoAndFeeList` — calls CBS opcode
 *  `CIB11000814` (via the old `DGBEBankingService.processTRN0111`).
 *
 *  NOT a plain pass-through: the old adapter fills in `validAccountYnDesc`/`feeResultYnDesc` on
 *  every returned item from `validAccountYn`/`feeResultYn` via `DataUtils.getResultYnDesc`
 *  (Y -> "Yes", N -> "No", anything else -> ""). Replicated in the Sbc. */
@RestController
@RequestMapping("/TRS1006")
class CheckReceiverAccountInfoAndFeeListCbc(
	private val sbc: CheckReceiverAccountInfoAndFeeListSbc,
) {
	@PostMapping
	fun inquire(
		@RequestBody request: RequestData<CheckReceiverAccountInfoAndFeeListRequest>,
	): ResponseData<CheckReceiverAccountInfoAndFeeListResponse> = sbc.inquire(request)
}
