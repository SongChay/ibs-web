package kh.bank.dgb.ibs.app.cbs.update_virtual_account_info

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Port of `VAC1004_Adapter_UpdateVirtualAccountInfo`. Old adapter force-blanked
 *  `depositStartHMS`/`depositEndHMS1`/`depositEndHMS2` to `""` before the CBS call, regardless of
 *  what the client sent — replicated below. */
@Service
class UpdateVirtualAccountInfoSbc(
	private val connector: CoreBankingApiConnector,
) {
	fun update(request: RequestData<UpdateVirtualAccountInfoRequest>): ResponseData<UpdateVirtualAccountInfoResponse> {
		val forcedBody = request.body?.copy(depositStartHMS = "", depositEndHMS1 = "", depositEndHMS2 = "")

		return connector.post(
			"CIB11302032",
			request.header?.languageCode,
			forcedBody,
			UpdateVirtualAccountInfoResponse::class.java,
		)
	}
}
