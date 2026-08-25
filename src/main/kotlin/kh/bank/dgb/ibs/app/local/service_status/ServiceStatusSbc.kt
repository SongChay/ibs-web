package kh.bank.dgb.ibs.app.local.service_status

import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Port of `ServiceStatusServiceImpl.getBlockingTime()` — the "is this final-approver/payment
 * action allowed right now" time-window check gating several money-movement adapters
 * (approval-by-final-approver, transfer/wing-transfer/oversea-transfer final approval, payroll
 * payment registration).
 *
 * CONSOLIDATED HERE: this logic never had its own Cbc/Sbc in the old app — each adapter that
 * needed it just called `ServiceStatusService` directly. When this project's ~130 CBS adapters
 * were ported in parallel batches, five different adapters (`ApprovalByFinalApproverSbc`,
 * `ExecuteTransferFinalApproverSbc`, `PayrollPaymentRegisterSbc`, `OverseaTransferFinalApprovalSbc`,
 * `WingTransferFinalApprovalSbc`) each independently re-implemented this same function, with
 * minor inconsistencies in edge-case handling (e.g. one used `!!` and would throw on unparseable
 * time strings, another safely returned "not allowed"). This is the one shared implementation —
 * the five call sites should be updated to use it instead of their private copies.
 *
 * Despite the old field's name (`ServiceStatusDTO.isBlockingTime`), the boolean this computes
 * actually means "the action IS allowed right now", not "currently blocked" — kept the exact
 * (confusing) legacy semantics, just given a clearer name here (`allowed`).
 */
@Service
class ServiceStatusSbc(
	private val serviceStatusRbc: ServiceStatusRbc,
) {

	data class BlockingTimeStatus(
		val allowed: Boolean,
		val description: String?,
	)

	data class ServiceOffStatus(
		val off: Boolean,
		val description: String?,
	)

	/**
	 * Port of `ServiceStatusServiceImpl.getServiceStatusOff()` — the whole-app maintenance-window
	 * gate checked once, at login (see `CustomAuthenticationSuccessHandler`): valid credentials
	 * still get turned away with a "service unavailable" error while this is on. A DIFFERENT row/
	 * axis than [getBlockingTime] — that one gates final-approver/payment actions during specific
	 * hours (`serviceStatusTypeCode` "11"); this one is a simple whole-system on/off flag
	 * (`serviceStatusTypeCode` "01").
	 */
	fun isServiceOff(): ServiceOffStatus {
		val row = serviceStatusRbc.getServiceStatus(SERVICE_STATUS_TYPE_CODE_ON_OFF)
		val off = row?.serviceStatusCode.equals(SERVICE_STATUS_OFF_CODE, ignoreCase = true)
		val description = row?.serviceStatusDescription?.trim()?.takeIf { it.isNotEmpty() }
			?: ResponseResultCodeType.SERVICE_STATUS_OFF.description
		return ServiceOffStatus(off = off, description = description)
	}

	/** Returns `null` if the status row itself isn't found for [serviceStatusTypeCode] (the old
	 *  app's `SERVICE_STATUS_NOT_FOUND` case) — callers should map that to that same result code. */
	fun getBlockingTime(serviceStatusTypeCode: String = SERVICE_STATUS_TIME_CODE): BlockingTimeStatus? {
		val row = serviceStatusRbc.getServiceStatus(serviceStatusTypeCode) ?: return null

		// Old code let unparseable from/to times propagate as an uncaught NumberFormatException
		// (undefined/500-ish behavior for what should be static admin-configured data). Treating
		// it as "not allowed" instead is a deliberate simplification, not a faithful copy of that
		// edge case.
		val fromTime = row.serviceStatusFromTime?.take(4)?.toIntOrNull()
			?: return BlockingTimeStatus(allowed = false, description = row.serviceStatusDescription)
		val toTime = row.serviceStatusToTime?.take(4)?.toIntOrNull()
			?: return BlockingTimeStatus(allowed = false, description = row.serviceStatusDescription)
		val currentHHMM = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")).toInt()

		// Ported verbatim from the old app, including the two branches under `fromTime > toTime`
		// that look redundant/possibly buggy — not "fixed" since the actual intended behavior
		// isn't obvious from the code alone.
		var isBetween = false
		if (fromTime > toTime) {
			if (fromTime > currentHHMM && currentHHMM < toTime) isBetween = true
			if (fromTime < currentHHMM && currentHHMM > toTime) isBetween = true
		}
		if (fromTime == toTime && fromTime == currentHHMM) isBetween = true
		if (fromTime < currentHHMM && currentHHMM < toTime) isBetween = true

		val allowed = when {
			row.serviceStatusCode.equals(SERVICE_STATUS_ON_CODE, ignoreCase = true) && isBetween -> true
			row.serviceStatusCode.equals(SERVICE_STATUS_NA_CODE, ignoreCase = true) -> true
			row.serviceStatusCode.equals(SERVICE_STATUS_OFF_CODE, ignoreCase = true) && !isBetween -> true
			else -> false
		}

		val description = row.serviceStatusDescription?.trim()?.takeIf { it.isNotEmpty() }
			?: ResponseResultCodeType.SERVICE_STATUS_TIME_OFF.description

		return BlockingTimeStatus(allowed = allowed, description = description)
	}

	companion object {
		/** Port of `BizResultCodeType.SERVICE_STATUS_TIME_CODE` — the row that gates final-approver/
		 *  payment execution hours. All five current callers use this same code; the parameter
		 *  exists in case a future feature needs a different status row. */
		const val SERVICE_STATUS_TIME_CODE = "11"
		/** Port of `BizResultCodeType.SERVICE_STATUS_ON_OFF_CODE` — the whole-app on/off row
		 *  checked by [isServiceOff]. Coincidentally also "01", like [SERVICE_STATUS_ON_CODE] below
		 *  — a different axis (row-selector vs. a code *value* within the time-window row), not a
		 *  duplicate. */
		private const val SERVICE_STATUS_TYPE_CODE_ON_OFF = "01"
		private const val SERVICE_STATUS_OFF_CODE = "00"
		private const val SERVICE_STATUS_ON_CODE = "01"
		private const val SERVICE_STATUS_NA_CODE = "02"
	}
}
