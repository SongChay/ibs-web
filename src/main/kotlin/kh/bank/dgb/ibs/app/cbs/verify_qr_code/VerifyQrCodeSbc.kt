package kh.bank.dgb.ibs.app.cbs.verify_qr_code

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class VerifyQrCodeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun verify(request: RequestData<VerifyQrCodeRequest>): ResponseData<VerifyQrCodeResponse> {
		return coreBankingApiConnector.post("CIB11000211", request.header?.languageCode, request.body, VerifyQrCodeResponse::class.java)
	}
}
