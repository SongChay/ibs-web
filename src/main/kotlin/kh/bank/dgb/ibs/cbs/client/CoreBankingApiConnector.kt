package kh.bank.dgb.ibs.cbs.client

import kh.bank.dgb.ibs.common.envelope.ResponseData

/**
 * Port of `DGBEBankingAPIConnector` — the boundary to the core-banking system (CBS) for actual
 * banking operations (account inquiries, transfers, ...), as opposed to `CoreBankingAuthClient`
 * which only handles the login credential check.
 *
 * `operationCode` is CBS's own service ID (the old app's `op`, e.g. `"ABM1001"`) — it becomes
 * both `commonBody.svcID` and part of `ifID`/the transaction GUID, matching the old wire format.
 * The base address is no longer a per-call parameter (it was always the same one configured
 * value in the old app anyway) — see `CoreBankingProperties.baseUrl`.
 */
interface CoreBankingApiConnector {
	fun <T1, T2> post(operationCode: String, languageCode: String?, requestBody: T1, responseBodyType: Class<T2>): ResponseData<T2>
}
