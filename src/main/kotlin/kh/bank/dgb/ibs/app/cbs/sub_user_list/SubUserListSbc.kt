package kh.bank.dgb.ibs.app.cbs.sub_user_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class SubUserListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<SubUserListRequest>): ResponseData<SubUserListResponse> {
		val result = coreBankingApiConnector.post("CIB11002301", request.header?.languageCode, request.body, SubUserListResponse::class.java)

		val enrichedList = result.body?.corporateSubUserInfoList?.map { user ->
			val lastLoginDate = toDdMmmYyyy(user.lastLoginDate)
			val lastLoginHms = toHhMmSsA(user.lastLoginHms)
			val lastAccessDate = if (lastLoginDate.isNotEmpty() && lastLoginHms.isNotEmpty()) "$lastLoginDate, $lastLoginHms" else ""
			user.copy(
				lastAccessDate = lastAccessDate,
				lastLoginDate = lastLoginDate,
				lastLoginHms = lastLoginHms,
				openDate = toDdMmmYyyy(user.openDate),
				serviceStatusDesc = serviceStatusDesc(user.serviceStatusCode),
			)
		}
		return result.copy(body = result.body?.copy(corporateSubUserInfoList = enrichedList))
	}

	// TODO: faithful port of `DateUtil.toDDMMMYYYY` (input pattern "yyyyMMdd", output "dd MMM
	// yyyy") — not exercised against real CBS data, so edge cases (blank/malformed input) are only
	// as well covered as the old try/catch-and-return-"" behavior implies.
	private fun toDdMmmYyyy(value: String?): String {
		if (value.isNullOrBlank()) return ""
		return runCatching {
			LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyyMMdd"))
				.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
		}.getOrDefault("")
	}

	// TODO: faithful port of `DateUtil.toHHMMSSA` (input pattern "HHmmssSSS", output "hh:mm:ss a").
	private fun toHhMmSsA(value: String?): String {
		if (value.isNullOrBlank()) return ""
		return runCatching {
			LocalTime.parse(value, DateTimeFormatter.ofPattern("HHmmssSSS"))
				.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH))
		}.getOrDefault("")
	}

	/** Port of `DataUtils.getServiceStatusDesc` / `ServiceStatusCodeType`. */
	private fun serviceStatusDesc(code: String?): String {
		return when (code) {
			"00" -> "Application"
			"01" -> "Active"
			"08" -> "Deactivated"
			"09" -> "Deactivated"
			else -> ""
		}
	}
}
