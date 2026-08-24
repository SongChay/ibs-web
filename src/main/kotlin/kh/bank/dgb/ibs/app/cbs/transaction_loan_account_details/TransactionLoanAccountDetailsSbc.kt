package kh.bank.dgb.ibs.app.cbs.transaction_loan_account_details

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionLoanAccountDetailsSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<TransactionLoanAccountDetailsRequest>): ResponseData<TransactionLoanAccountDetailsResponse> {
		val result = connector.post("CIB11300711", request.header?.languageCode, request.body, TransactionLoanAccountDetailsResponse::class.java)

		val body = result.body ?: return result
		val fromDate = request.body?.fromDate?.let { runCatching { BigDecimal(it) }.getOrNull() }
		val toDate = request.body?.toDate?.let { runCatching { BigDecimal(it) }.getOrNull() }

		val filtered = body.accountList.orEmpty()
			.filter { item ->
				val paymentDate = item.paymentDate?.let { runCatching { BigDecimal(it) }.getOrNull() }
				paymentDate != null && fromDate != null && toDate != null &&
					fromDate <= paymentDate && toDate >= paymentDate
			}
			.map { item -> item.copy(paymentStatusDescription = paymentStatusDescription(item.paymentStatusCode)) }

		return ResponseData(
			header = result.header,
			body = body.copy(accountList = filtered, totalCount = filtered.size.toLong()),
		)
	}

	/** Port of `DataUtils.getPaymentStatusDescription` / `type.PaymentStatusCode`. */
	private fun paymentStatusDescription(code: String?): String = when (code) {
		"0" -> "Unpaid"
		"1" -> "Paid"
		"2" -> "Proportion"
		"3" -> "Early Paid"
		else -> ""
	}
}
