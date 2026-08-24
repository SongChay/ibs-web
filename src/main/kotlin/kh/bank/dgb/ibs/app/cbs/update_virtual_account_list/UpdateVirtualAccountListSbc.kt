package kh.bank.dgb.ibs.app.cbs.update_virtual_account_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.math.BigDecimal

/** Port of `VAC1002_REQ_UpdateVirtualAccountListVo` list wrapped for the CBS wire — the client
 *  sends the list under `virtualAccountList`, but CBS expects it under `grid01`
 *  (`@JsonGetter("grid01")` in the old Vo). Kept as a distinct wire-shaped type rather than a
 *  rename, matching how CBS response grids (e.g. `RetrieveListWidgetCbsResponse`) are handled. */
data class UpdateVirtualAccountListCbsItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerDescription: String? = null,
	val depositStartDate: String? = null,
	val depositStartHMS: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val monthlyRepetitionYN: String? = null,
	val depositEndDate1: String? = null,
	val depositEndHMS1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val depositEndDate2: String? = null,
	val depositEndHMS2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val accountExpiryDate: String? = null,
)

data class UpdateVirtualAccountListCbsRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val beginningFlag: String? = null,
	val endFlag: String? = null,
	val grid01: List<UpdateVirtualAccountListCbsItem>? = null,
)

/**
 * Port of `VAC1002_Adapter_UpdateVirtualAccountList`.
 *
 * Extra logic beyond a plain pass-through: every item has `depositStartHMS`/`depositEndHMS1`/
 * `depositEndHMS2` force-blanked to `""` before the CBS call, regardless of what the client sent —
 * replicated below. (Old adapter also logged batch size/timing around the call; that's
 * infrastructure logging, not business logic, and isn't replicated.)
 */
@Service
class UpdateVirtualAccountListSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<UpdateVirtualAccountListRequest>): ResponseData<UpdateVirtualAccountListResponse> {
		val body = request.body

		val cbsItems = body?.virtualAccountList?.map {
			UpdateVirtualAccountListCbsItem(
				virtualAccountNo = it.virtualAccountNo,
				parentAccountNo = it.parentAccountNo,
				paymentName = it.paymentName,
				customerUniqueNo = it.customerUniqueNo,
				customerDescription = it.customerDescription,
				depositStartDate = it.depositStartDate,
				depositStartHMS = "",
				virtualAccountReceiveTypeCode = it.virtualAccountReceiveTypeCode,
				monthlyRepetitionYN = it.monthlyRepetitionYN,
				depositEndDate1 = it.depositEndDate1,
				depositEndHMS1 = "",
				registerAmount1 = it.registerAmount1,
				depositEndDate2 = it.depositEndDate2,
				depositEndHMS2 = "",
				registerAmount2 = it.registerAmount2,
				accountExpiryDate = it.accountExpiryDate,
			)
		}

		val cbsRequest = UpdateVirtualAccountListCbsRequest(
			userID = body?.userID,
			customerNo = body?.customerNo,
			beginningFlag = body?.beginningFlag,
			endFlag = body?.endFlag,
			grid01 = cbsItems,
		)

		return connector.post(
			"CIB11302031",
			request.header?.languageCode,
			cbsRequest,
			UpdateVirtualAccountListResponse::class.java,
		)
	}
}
