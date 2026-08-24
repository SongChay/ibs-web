package kh.bank.dgb.ibs.app.cbs.wing_transfer_registration

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class WingTransferListItem(
	val amount: BigDecimal? = null,
	val recipientName: String? = null,
	val toAccountNumber: String? = null,
	val receiverPhoneNo: String? = null,
	val receiverCountryCode: String? = null,
	val wingTransferTypeCode: String? = null,
	val receiverAccountRemark: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class WingTransferApprovalLineItem(
	val approverTypeCode: String? = null,
	val approverID: String? = null,
)

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

/** Port of `TRS5101_REQ_RegisterWingTransferVo` — `transferList`/`approvalList` are accepted from
 *  the client under those names but forwarded to CBS as `grid01`/`grid02` (`@JsonSetter`/
 *  `@JsonGetter` split in the old Vo — `@param` controls the inbound key, `@get` controls the
 *  outbound-to-CBS key, since this same object is both the `@RequestBody` shape and the exact
 *  body forwarded via `connector.post`). */
data class WingTransferRegistrationRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val previousApprovalNo: Long? = null,
	@param:JsonProperty("transferList") @get:JsonProperty("grid01")
	val transferList: List<WingTransferListItem>? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid02")
	val approvalList: List<WingTransferApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class WingTransferResultItem(
	val isRefund: Boolean? = null,
	val receiverName: String? = null,
	val receiverAccount: String? = null,
	val receiverPhone: String? = null,
	val code: String? = null,
	val resultMessage: String? = null,
)

/** Port of `TRS5101_RES_RegisterWingTransferVo` — `wingTransferResult` carries a single
 *  (non-split) `@JsonProperty("grid01")` on the old Java field, so the wire key is `grid01` in
 *  *both* directions here, not just one. */
data class WingTransferRegistrationResponse(
	val approvalNo: Long? = null,
	@JsonProperty("grid01")
	val wingTransferResult: List<WingTransferResultItem>? = null,
)

/**
 * Port of `TRS5101_Adapter_WingTransfer` — calls CBS opcode `CIB11001921` (the old
 * `DGBEBankingService.processAPR0040`).
 *
 * Small extra logic beyond a pure pass-through: the old adapter force-sets
 * `receiverCountryCode = "KHM"` on every item in the transfer list before calling CBS (Wing
 * transfers are always domestic/Cambodia). Replicated in `WingTransferRegistrationSbc`.
 */
@RestController
@RequestMapping("/TRS5101")
class WingTransferRegistrationCbc(
	private val sbc: WingTransferRegistrationSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<WingTransferRegistrationRequest>): ResponseData<WingTransferRegistrationResponse> =
		sbc.register(request)
}
