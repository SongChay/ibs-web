package kh.bank.dgb.ibs.app.cbs.inquiry_favorite_menu_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class InquiryFavoriteMenuListRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
)

data class InquiryFavoriteMenuItem(
	val userID: String? = null,
	val seqNo: Int = 0,
	val level1MenuCode: String? = null,
	val level2MenuCode: String? = null,
	val level1MenuDescription: String? = null,
	val level2MenuDescription: String? = null,
	val registerDate: String? = null,
)

/** Port of `GNB1003_RES_WrappterInquiryFavoriteMenuListVo` — wire name `grid01` (declared only on
 *  the getter in the old Vo, which Jackson treats as the property's name for both directions). */
data class InquiryFavoriteMenuListResponse(
	@JsonProperty("grid01") val userFavoriteMenuInfoList: List<InquiryFavoriteMenuItem>? = null,
)

/** Port of `GNB1003_Adapter_InquiryFavoriteMenuList` — calls CBS opcode `CIB11300511` (via the old
 *  `DGBEBankingService.processCIB11300511`). `channelTypeCode` is forced to the fixed corporate
 *  banking channel code "01" before forwarding, same as the old adapter. */
@RestController
@RequestMapping("/GNB1003")
class InquiryFavoriteMenuListCbc(
	private val sbc: InquiryFavoriteMenuListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<InquiryFavoriteMenuListRequest>): ResponseData<InquiryFavoriteMenuListResponse> =
		sbc.inquire(request)
}
