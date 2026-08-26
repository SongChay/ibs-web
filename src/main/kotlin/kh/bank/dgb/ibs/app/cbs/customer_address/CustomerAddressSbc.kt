package kh.bank.dgb.ibs.app.cbs.customer_address

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class CustomerAddressSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<CustomerAddressRequest>): ResponseData<CustomerAddressResponse> {
		return coreBankingApiConnector.post("CIB11302512", request.header?.languageCode, request.body, CustomerAddressResponse::class.java)
	}
}
