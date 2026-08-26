package kh.bank.dgb.ibs.app.cbs.check_receiver_account_info_and_fee_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CheckReceiverAccountInfoAndFeeListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CheckReceiverAccountInfoAndFeeListRequest>): ResponseData<CheckReceiverAccountInfoAndFeeListResponse> {
		val response = coreBankingApiConnector.post(
			"CIB11000814",
			request.header?.languageCode,
			request.body,
			CheckReceiverAccountInfoAndFeeListResponse::class.java,
		)

		val enrichedAccountList = response.body?.accountList?.map { item ->
			item.copy(
				validAccountYnDesc = resultYnDesc(item.validAccountYn),
				feeResultYnDesc = resultYnDesc(item.feeResultYn),
			)
		}

		return if (enrichedAccountList == null) {
			response
		} else {
			response.copy(body = response.body?.copy(accountList = enrichedAccountList))
		}
	}

	/** Port of `DataUtils.getResultYnDesc`. */
	private fun resultYnDesc(resultYn: String?): String {
		return when {
			resultYn.equals("Y", ignoreCase = false) -> "Yes"
			resultYn.equals("N", ignoreCase = false) -> "No"
			else -> ""
		}
	}
}
