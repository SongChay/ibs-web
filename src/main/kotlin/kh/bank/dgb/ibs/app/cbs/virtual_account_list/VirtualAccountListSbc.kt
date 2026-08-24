package kh.bank.dgb.ibs.app.cbs.virtual_account_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar

/** Port of `VAC1001_RES_VirtualAccountListVo` — the CBS wire shape. `customerDescription` is
 *  read back from CBS under the key `customerFullName` (`@JsonSetter`) but re-exposed to the
 *  client as `customerDescription` (`@JsonGetter`) — a real asymmetry, kept as two distinct field
 *  names rather than collapsed. */
data class VirtualAccountListCbsItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerFullName: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val virtualAccountReceiveType: String? = null,
	val currencyCode: String? = null,
	val monthlyRepetitionYN: String? = null,
	val depositStartDate: String? = null,
	val depositStartHMS: String? = null,
	val depositEndDate1: String? = null,
	val depositEndHMS1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val useYN: String? = null,
	val depositEndDate2: String? = null,
	val depositEndHMS2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val virtualAccountReceiveStatusCode: String? = null,
	val virtualAccountReceiveStatus: String? = null,
	val depositDate: String? = null,
	val balanceAmount: BigDecimal? = null,
	val accountExpiryDate: String? = null,
)

/** Port of `VAC1001_RES_WrapperVirtualAccountListVo` — the CBS wire shape (`grid01`). */
data class VirtualAccountListCbsResponse(
	val totalCount: Long? = null,
	val grid01: List<VirtualAccountListCbsItem>? = null,
)

/**
 * Port of `VAC1001_Adapter_InquiryVirtualAccountList`.
 *
 * Extra logic beyond a plain pass-through: any item with a blank `accountExpiryDate` gets it
 * defaulted to "today + 99 years" (`yyyyMMdd`), replicated below.
 */
@Service
class VirtualAccountListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<VirtualAccountListRequest>): ResponseData<VirtualAccountListResponse> {
		val cbsResult = connector.post(
			"CIB11302011",
			request.header?.languageCode,
			request.body,
			VirtualAccountListCbsResponse::class.java,
		)

		val items = cbsResult.body?.grid01?.map {
			VirtualAccountListItem(
				virtualAccountNo = it.virtualAccountNo,
				parentAccountNo = it.parentAccountNo,
				paymentName = it.paymentName,
				customerUniqueNo = it.customerUniqueNo,
				customerDescription = it.customerFullName,
				virtualAccountReceiveTypeCode = it.virtualAccountReceiveTypeCode,
				virtualAccountReceiveType = it.virtualAccountReceiveType,
				currencyCode = it.currencyCode,
				monthlyRepetitionYN = it.monthlyRepetitionYN,
				depositStartDate = it.depositStartDate,
				depositStartHMS = it.depositStartHMS,
				depositEndDate1 = it.depositEndDate1,
				depositEndHMS1 = it.depositEndHMS1,
				registerAmount1 = it.registerAmount1,
				useYN = it.useYN,
				depositEndDate2 = it.depositEndDate2,
				depositEndHMS2 = it.depositEndHMS2,
				registerAmount2 = it.registerAmount2,
				virtualAccountReceiveStatusCode = it.virtualAccountReceiveStatusCode,
				virtualAccountReceiveStatus = it.virtualAccountReceiveStatus,
				depositDate = it.depositDate,
				balanceAmount = it.balanceAmount,
				accountExpiryDate = it.accountExpiryDate?.trim()?.ifEmpty { null } ?: defaultExpiryDate(),
			)
		}

		return ResponseData(
			header = cbsResult.header,
			body = VirtualAccountListResponse(totalCount = cbsResult.body?.totalCount, virtualAccountList = items),
		)
	}

	private fun defaultExpiryDate(): String {
		val calendar = Calendar.getInstance()
		calendar.add(Calendar.YEAR, 99)
		return SimpleDateFormat("yyyyMMdd").format(calendar.time)
	}
}
