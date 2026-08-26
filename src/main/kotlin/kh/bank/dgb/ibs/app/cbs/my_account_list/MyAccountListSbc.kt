package kh.bank.dgb.ibs.app.cbs.my_account_list

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of `TRS1401_Adapter_InquiryMyAccountList#process`. Beyond the plain CBS pass-through, the
 * old adapter enriches every item with description fields derived from the old `DataUtils` helper
 * (not ported elsewhere, so the two small lookup tables it used are replicated locally below).
 */
@Service
class MyAccountListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<MyAccountListRequest>): ResponseData<MyAccountListResponse> {
		val response = coreBankingApiConnector.post("CIB11001201", request.header?.languageCode, request.body, MyAccountListResponse::class.java)
		val enrichedList = response.body?.accountList?.map { item ->
			item.copy(
				depositSubjectDescription = depositSubjectDescription(item.depositSubjectCode),
				depositAccountStatusDescription = depositAccountStatusDescription(item.depositAccountStatusCode),
			)
		}
		return response.copy(body = response.body?.copy(accountList = enrichedList))
	}

	private fun depositSubjectDescription(code: String?): String {
		return when (code) {
			"110" -> "Current Account"
			"120" -> "Saving Account"
			"130" -> "Fixed Account"
			"140" -> "Installment Account"
			"150" -> "Loan Account"
			else -> ""
		}
	}

	private fun depositAccountStatusDescription(code: String?): String {
		return when (code) {
			"01" -> "Normal"
			"04" -> "Transaction Suspended"
			"06" -> "Blocked"
			"07" -> "Normally Terminated"
			"09" -> "Terminated with other reason"
			"10" -> "LumpsumLedger"
			"21" -> "Terminated on maturity"
			"22" -> "Early Termination"
			"32" -> "Netting on maturity"
			"40" -> "Terminated due to Branch transfer"
			"50" -> "Account closed"
			"99" -> "New registratoin cancelled"
			else -> ""
		}
	}
}
