package kh.bank.dgb.ibs.app.cbs.virtual_account_history_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Locale

/** Port of `VAC3001_RES_WrapperVirtualAccountHistoryListVo` — the CBS wire shape. `totalCount`
 *  reads back under `grid01Count` and the list under `grid01` (old Vo's `@JsonSetter`s); the
 *  client-facing names are `totalCount`/`virtualAccountPaymentHistoryList` (old Vo's
 *  `@JsonGetter`s). Item fields themselves have no such rename. */
data class VirtualAccountHistoryListCbsResponse(
	val grid01Count: Long? = null,
	val grid01: List<VirtualAccountHistoryItem>? = null,
)

/**
 * Port of `VAC3001_Adapter_InquiryVirtualAccountHistoryList`.
 *
 * Extra logic beyond a plain pass-through, both replicated below:
 *  1. Five date fields (`paymentInfoDate`, `depositStartDate`, `depositEndDate1`,
 *     `depositEndDate2`, `accountExpiryDate`) are reformatted from CBS's `yyyyMMdd` wire format to
 *     `dd MMM yyyy` for display (`DateUtil.toDDMMMYYYY`) — blank/unparseable input becomes `""`,
 *     matching the old util's exception-swallowing behavior.
 *  2. `virtualAccountReceiveStatusDescription` isn't populated by CBS at all — it's computed
 *     client-side from `virtualAccountReceiveStatusCode` via `DataUtils
 *     .getVirtualAccountReceiveStatusDescription`. NOTE: the old mapping has `RECEIEVING` ("01")
 *     also describing as "Unpaid" (same text as `UNPAID`/"00") — almost certainly a copy-paste bug
 *     in the old `VirtualAccountReceiveStatusCode` enum, but replicated verbatim rather than
 *     "fixed" here, since correcting wire-facing text is outside this port's scope.
 */
@Service
class VirtualAccountHistoryListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<VirtualAccountHistoryListRequest>): ResponseData<VirtualAccountHistoryListResponse> {
		val cbsResult = connector.post(
			"CIB11302111",
			request.header?.languageCode,
			request.body,
			VirtualAccountHistoryListCbsResponse::class.java,
		)

		val items = cbsResult.body?.grid01?.map {
			it.copy(
				paymentInfoDate = toDdMmmYyyy(it.paymentInfoDate),
				depositStartDate = toDdMmmYyyy(it.depositStartDate),
				depositEndDate1 = toDdMmmYyyy(it.depositEndDate1),
				depositEndDate2 = toDdMmmYyyy(it.depositEndDate2),
				accountExpiryDate = toDdMmmYyyy(it.accountExpiryDate),
				virtualAccountReceiveStatusDescription = receiveStatusDescription(it.virtualAccountReceiveStatusCode),
			)
		}

		return ResponseData(
			header = cbsResult.header,
			body = VirtualAccountHistoryListResponse(
				totalCount = cbsResult.body?.grid01Count,
				virtualAccountPaymentHistoryList = items,
			),
		)
	}

	/** Port of `DateUtil.toDDMMMYYYY` — parses `yyyyMMdd`, formats `dd MMM yyyy`; returns `""` for
	 *  blank or unparseable input, same as the old util's swallowed-exception behavior. */
	private fun toDdMmmYyyy(source: String?): String {
		if (source.isNullOrBlank()) return ""
		return runCatching {
			val parsed = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(source.trim())
			SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(parsed)
		}.getOrDefault("")
	}

	/** Port of `DataUtils.getVirtualAccountReceiveStatusDescription`. */
	private fun receiveStatusDescription(code: String?): String = when (code) {
		"00" -> "Unpaid" // VirtualAccountReceiveStatusCode.UNPAID
		"01" -> "Unpaid" // VirtualAccountReceiveStatusCode.RECEIEVING (sic — same text as UNPAID in the old app)
		"02" -> "Paid" // VirtualAccountReceiveStatusCode.COMPLETE
		else -> ""
	}
}
