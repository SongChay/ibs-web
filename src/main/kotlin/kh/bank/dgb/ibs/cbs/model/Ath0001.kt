package kh.bank.dgb.ibs.cbs.model

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo

/** Port of `ATH0001ReqDTO` — sent to CBS opcode `CIB11300191` (real login), password already
 *  RSA-encrypted with CBS's own key (see `ChannelRsaUtils`/`CoreBankingRsaClient`). */
data class Ath0001Request(
	val userID: String? = null,
	val userPwd: String? = null,
	val channelTypeCode: String? = null,
)

/**
 * Port of `ATH0001ResDTO` — the full corporate-user profile CBS hands back on successful login.
 * Restored from the earlier simplified true/false-only contract; this is what gets stored in
 * session and echoed to the client (see `CustomAuthenticationSuccessHandler`).
 *
 * Several fields are genuinely asymmetric on the wire — CBS sends one casing when deserializing
 * in, the old app serialized a different casing back out to its own client — exactly the same
 * `gridNN`-style defect class already reviewed elsewhere in this codebase, so given the same
 * verified fix: `@param:JsonProperty` (deserialize, CBS-facing) + `@get:JsonProperty` (serialize,
 * client-facing) BOTH present, never a bare one alone.
 */
data class Ath0001Response(
	val userID: String? = null,
	val userName: String? = null,
	val customerNo: String? = null,
	@param:JsonProperty("firstAuthenticationYN") @get:JsonProperty("firstAuthenticationYn")
	val firstAuthenticationYn: String? = null,
	val customerName: String? = null,
	@param:JsonProperty("nationalID") @get:JsonProperty("nationalCode")
	val nationalCode: String? = null,
	val phoneNo: String? = null,
	val serviceStatusCode: String? = null,
	val serviceCeaseReasonCode: String? = null,
	val customerTypeCode: String? = null,
	val securityMediaTypeCode: String? = null,
	val lastLoginDate: String? = null,
	@param:JsonProperty("lastLoginHMS") @get:JsonProperty("lastLoginHms")
	val lastLoginHms: String? = null,
	val passwordErrorCount: Int? = null,
	val passwordChangeDate: String? = null,
	@param:JsonProperty("passwordChangeRequiredYN") @get:JsonProperty("passwordChangeRequiredYn")
	val passwordChangeRequiredYn: String? = null,
	val maxPasswordErrorCount: Int? = null,
	@param:JsonProperty("transferBlockYN") @get:JsonProperty("transferBlockYn")
	val transferBlockYn: String? = null,
	@param:JsonProperty("subIDYN") @get:JsonProperty("subIDYn")
	val subIDYn: String? = null,
	@param:JsonProperty("oTPCreateRequiredYN") @get:JsonProperty("otpCreateRequiredYn")
	val otpCreateRequiredYn: String? = null,
	val accountManagementTypeCode: String? = null,
	val corporateUserProfileImageURL: String? = null,
	val corporateName: String? = null,
	/** Set locally after login (`session.id`) — never actually populated by CBS. */
	val tkn: String? = null,
	val operationMasterYN: String? = null,
)

/** Outcome of `CoreBankingAuthClient.authenticate` — CBS answers with either a full profile
 *  (success) or a result code/message (failure, already message-substituted with the account's
 *  password-attempt counts, matching the old `AuthenticationProviderImpl`'s `StrSubstitutor` use). */
sealed interface Ath0001Result {
	data class Success(val header: ResponseUserHeaderVo, val profile: Ath0001Response) : Ath0001Result
	data class Failure(val resultCode: String?, val resultMessage: String?) : Ath0001Result
}
