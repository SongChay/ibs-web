package kh.bank.dgb.ibs.app.cbs.inquiry_deposit_status

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class InquiryDepositStatusSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(
		request: RequestData<InquiryDepositStatusRequest>,
	): ResponseData<InquiryDepositStatusResponse> {
		val result = coreBankingApiConnector.post(
			"CIB11300412",
			request.header?.languageCode,
			request.body,
			InquiryDepositStatusResponse::class.java,
		)

		if (result.header?.result != true) {
			return result
		}

		val enrichedList = result.body?.accountList?.map { item ->
			item.copy(
				depositSubjectDescription = depositSubjectDescription(item.depositSubjectCode),
				depositAccountStatusDescription = depositAccountStatusDescription(item.depositAccountStatusCode),
				repaymentMethodDescription = repaymentMethodDescription(item.repaymentMethodCode),
			)
		}

		return result.copy(body = result.body?.copy(accountList = enrichedList))
	}

	/** Port of `DataUtils.getDepositSubjectDescription` (old `DepositSubjectCode` enum). Returns
	 *  `""` for an unrecognized/null code, matching the old lookup-chain-with-no-else behaviour. */
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

	/** Port of `DataUtils.getDepositAccountStatusDescription` (old `DepositAccountStatusCode`
	 *  enum). Returns `""` for an unrecognized/null code. */
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
			"99" -> "New registratoin cancelled" // sic — matches old code's typo verbatim
			else -> ""
		}
	}

	/** Port of `DataUtils.getRepaymentMethodDescription` (old `RepaymentMethodCode` enum).
	 *  Returns `""` for an unrecognized/null code. */
	private fun repaymentMethodDescription(code: String?): String {
		return when (code) {
			"10" -> "Bullet"
			"20" -> "Installment"
			"30" -> "Amortization"
			"40" -> "Negotiable"
			"90" -> "Other(OD)"
			else -> ""
		}
	}
}
