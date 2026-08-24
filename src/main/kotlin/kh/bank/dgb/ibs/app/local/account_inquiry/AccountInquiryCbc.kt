package kh.bank.dgb.ibs.app.local.account_inquiry

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Template feature slice — Controller (`Cbc` suffix).
 *
 * One feature = one folder under `app/`, named for what it does (snake_case), holding exactly
 * three classes:
 *   - `<Feature>Cbc` (this file)  — HTTP boundary only: bind request/response, delegate to Sbc.
 *   - `<Feature>Sbc`              — business logic, orchestrates Rbc + any external calls.
 *   - `<Feature>Rbc`              — MyBatis mapper interface, DB access only.
 *
 * This replaces the old `MainController` + `DefaultAdapterDispatcher` reflective routing (one
 * `@Adapter`-annotated class per operation, dispatched by URL path via reflection) with a real
 * Spring MVC controller per feature. Each of the 143 old adapters becomes one such folder.
 *
 * Request/response are wrapped in `RequestData<T>`/`ResponseData<T>` — NOT plain DTOs — because
 * the existing client can't change and still expects the legacy `{"header": ..., "body": ...}`
 * envelope with a `resultCode`/`resultMessage` on every response. `EncryptedEnvelopeFilter`
 * handles the AES layer transparently on top of this; the controller itself never touches
 * encryption directly.
 *
 * Placeholder logic below — replace with the real port of whichever ABMxxxx adapter this feature
 * corresponds to.
 *
 * NOTE: this is a TEMPLATE/placeholder feature only — a fake account-balance lookup written
 * before the "route == old adapter's literal route" convention (see e.g. `NewsEventCbc`,
 * `FaqCbc`) was established. It does NOT correspond to any real old adapter/`@Adapter(route=...)`
 * in the legacy app, so its `/api/account-inquiry` path is left as-is rather than being pointed at
 * a fabricated legacy route. Do not use this file as a model for wiring a real adapter's route —
 * copy the pattern from one of the ported features instead.
 */
@RestController
@RequestMapping("/api/account-inquiry")
class AccountInquiryCbc(
	private val accountInquirySbc: AccountInquirySbc,
) {

	@PostMapping
	fun inquire(@RequestBody request: RequestData<AccountInquiryRequest>): ResponseData<AccountInquiryResponse> {
		val body = accountInquirySbc.inquire(request.body!!)
		return ResponseData(
			header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS),
			body = body,
		)
	}
}

data class AccountInquiryRequest(
	val accountNo: String,
)

data class AccountInquiryResponse(
	val accountNo: String,
	val balance: java.math.BigDecimal,
)
