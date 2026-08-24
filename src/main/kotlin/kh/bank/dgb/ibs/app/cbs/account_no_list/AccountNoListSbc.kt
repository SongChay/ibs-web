package kh.bank.dgb.ibs.app.cbs.account_no_list

import kh.bank.dgb.ibs.app.cbs.all_account_inquiry_list.AllAccountInquiryListRequest
import kh.bank.dgb.ibs.app.cbs.all_account_inquiry_list.AllAccountInquiryListSbc
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class AccountNoListSbc(
	private val allAccountInquiryListSbc: AllAccountInquiryListSbc,
) {
	fun inquire(request: RequestData<AllAccountInquiryListRequest>): ResponseData<AccountNoListResponse> {
		val result = allAccountInquiryListSbc.fetchFilteredAccounts(request)

		val projected = result.body?.accountList.orEmpty().map { item ->
			AccountNoListItem(
				accountNo = item.accountNo,
				accountName = item.accountName,
				accountNickName = item.accountNickName,
				currencyCode = item.currencyCode,
			)
		}

		return ResponseData(header = result.header, body = AccountNoListResponse(accountList = projected))
	}
}
