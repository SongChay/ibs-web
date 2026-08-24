package kh.bank.dgb.ibs.app.cbs.save_sub_user

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/** Internal-only shapes for the intermediate "check duplicated user ID" call this feature makes
 *  before deciding whether to add or update — never exposed on `/api/save-sub-user` itself. */
private data class CheckDuplicatedUserRequest(val userID: String? = null)
private data class CheckDuplicatedUserResponse(val resultYn: String? = null)

@Service
class SaveSubUserSbc(
	private val connector: CoreBankingApiConnector,
) {
	/**
	 * Port of `INF2104_Adapter_SaveSubUser.process`. Steps, matching the old code exactly:
	 *  1. For every `userAccountAccessInfoList` item, derive `accountAccessRightTypeCode`: if
	 *     `accountFullAccessRightTypeCode <= accountInquiryAccessRightTypeCode` (as integers), use
	 *     the inquiry code, otherwise use the full-access code. (Copied verbatim; this reads oddly
	 *     but is exactly what the old adapter did.)
	 *  2. Call CBS opcode `CIB11002411` ("check duplicated user ID") for `request.body.userID`.
	 *  3. If that call's `header.result == true`, call `CIB11302421` (add sub user); otherwise
	 *     call `CIB11302431` (update sub user). NOTE: this branch condition is copied exactly as
	 *     the old adapter had it — a `true` "check duplicated" result routing to *add* reads
	 *     backwards from what the CBS operation's name implies. Not "fixed" here; flagged for
	 *     scrutiny same as in the Cbc doc comment.
	 */
	fun save(request: RequestData<SaveSubUserRequest>): ResponseData<SaveSubUserResponse> {
		val body = request.body

		val derivedAccountAccessList = body?.userAccountAccessInfoList?.map { item ->
			val fullAccess = item.accountFullAccessRightTypeCode?.toIntOrNull()
			val inquiryAccess = item.accountInquiryAccessRightTypeCode?.toIntOrNull()
			val accessCode = if (fullAccess != null && inquiryAccess != null && fullAccess <= inquiryAccess) {
				item.accountInquiryAccessRightTypeCode
			} else {
				item.accountFullAccessRightTypeCode
			}
			item.copy(accountAccessRightTypeCode = accessCode)
		}
		val effectiveBody = body?.copy(userAccountAccessInfoList = derivedAccountAccessList)

		val checkDuplicateResult = connector.post(
			"CIB11002411",
			request.header?.languageCode,
			CheckDuplicatedUserRequest(userID = body?.userID),
			CheckDuplicatedUserResponse::class.java,
		)

		return if (checkDuplicateResult.header?.result == true) {
			// add sub user
			connector.post("CIB11302421", request.header?.languageCode, effectiveBody, SaveSubUserResponse::class.java)
		} else {
			// update sub user
			connector.post("CIB11302431", request.header?.languageCode, effectiveBody, SaveSubUserResponse::class.java)
		}
	}
}
