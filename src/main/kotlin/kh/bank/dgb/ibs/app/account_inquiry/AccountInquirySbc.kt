package kh.bank.dgb.ibs.app.account_inquiry

import org.springframework.stereotype.Service

/**
 * Template feature slice — Service (`Sbc` suffix).
 *
 * Business logic for this feature: validation, orchestration between the Rbc (DB) and any
 * external call (e.g. the old `DGBEBankingAPIConnector` core-banking connector), and mapping
 * to the response shape the Cbc returns. Keep HTTP concerns (status codes, headers) out of here —
 * that's the Cbc's job — so this class stays testable without spinning up MVC.
 */
@Service
class AccountInquirySbc(
	private val accountInquiryRbc: AccountInquiryRbc,
) {

	fun inquire(request: AccountInquiryRequest): AccountInquiryResponse {
		val balance = accountInquiryRbc.findBalanceByAccountNo(request.accountNo)
		return AccountInquiryResponse(
			accountNo = request.accountNo,
			balance = balance,
		)
	}
}
