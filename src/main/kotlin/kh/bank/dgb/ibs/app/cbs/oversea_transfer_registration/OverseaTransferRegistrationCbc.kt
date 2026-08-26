package kh.bank.dgb.ibs.app.cbs.oversea_transfer_registration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class OverseaTransferApprovalItem(
	val approverID: String? = null,
	val approverTypeCode: String? = null,
)

data class OverseaTransferFileItem(
	val filePath: String? = null,
	val fileName: String? = null,
	val originalFileName: String? = null,
	val fileExtension: String? = null,
)

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

/**
 * Port of `TRS4101_REQ_RegisterOverseaTransferVo`.
 *  - `approvalList`/`fileList` are accepted from the client under those names but forwarded to
 *    CBS as `grid01`/`grid02` (`@JsonSetter`/`@JsonGetter` split in the old Vo — `@param`
 *    controls the inbound key, `@get` controls the outbound-to-CBS key, since this same object is
 *    both the `@RequestBody` shape and the exact body forwarded via `connector.post`).
 *  - `purposeTransfer` was `@JsonIgnore` on the old getter: accepted from the client but NEVER
 *    forwarded to CBS (only `purposeCode` reaches CBS). Replicated with `@get:JsonIgnore` — same
 *    effect, since this DTO's getter is exactly what gets serialized when forwarded to CBS.
 */
data class OverseaTransferRegistrationRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val senderAddress: String? = null,
	val senderPhoneNo: String? = null,
	val senderCardID: String? = null,
	val receiverBankAddress: String? = null,
	val receiverBankSwiftCode: String? = null,
	val receiverBankPostalCode: String? = null,
	val receiverBankTownName: String? = null,
	val overseaReceiverAccountNo: String? = null,
	val counterpartBankAccountNumber: String? = null,
	val receiverAddress: String? = null,
	val receiverName: String? = null,
	val receiverPostalCode: String? = null,
	val receiverTownName: String? = null,
	val receiverRemark: String? = null,
	val amount: BigDecimal? = null,
	val overseaCommissionTypeCode: String? = null,
	@get:JsonIgnore
	val purposeTransfer: String? = null,
	val invoiceNo: String? = null,
	val purposeCode: String? = null,
	val remark: String? = null,
	val overseaReceiverBankName: String? = null,
	val overseaReceiverBankCountryID: String? = null,
	val approvalMemo: String? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid01")
	val approvalList: List<OverseaTransferApprovalItem>? = null,
	@param:JsonProperty("fileList") @get:JsonProperty("grid02")
	val fileList: List<OverseaTransferFileItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class OverseaTransferRegistrationResponse(
	val approvalNo: Long? = null,
)

/** Port of `TRS4101_Adapter_RegisterOverseaTransfer` — calls CBS opcode `CIB11301721` (the old
 *  `DGBEBankingService.processAPR0030`). Money movement (registers an oversea transfer for
 *  approval), but the adapter itself is a straight pass-through to CBS. */
@RestController
@RequestMapping("/TRS4101")
class OverseaTransferRegistrationCbc(
	private val overseaTransferRegistrationSbc: OverseaTransferRegistrationSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<OverseaTransferRegistrationRequest>): ResponseData<OverseaTransferRegistrationResponse> {
		return overseaTransferRegistrationSbc.register(request)
	}
}
