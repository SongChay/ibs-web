package kh.bank.dgb.ibs.app.local.service_status

import org.apache.ibatis.annotations.Mapper

/** Port of `ServiceStatusDTO` (persistence fields only — `isServiceStatusOff`/`isBlockingTime`
 *  in the old DTO were computed business flags derived from `serviceStatusFromTime/ToTime`, not
 *  stored columns; that logic belongs in a service layer once this feature gets a Cbc/Sbc, not
 *  the DAO). */
data class ServiceStatus(
	val serviceStatusTypeCode: String,
	val serviceStatusCode: String? = null,
	val serviceStatusFromTime: String? = null,
	val serviceStatusToTime: String? = null,
	val serviceStatusDescription: String? = null,
	val updatedBy: String? = null,
	val updatedDate: String? = null,
)

/** Port of `ServiceStatusServiceDAO`. */
@Mapper
interface ServiceStatusRbc {
	fun getServiceStatus(serviceStatusTypeCode: String): ServiceStatus?
}
