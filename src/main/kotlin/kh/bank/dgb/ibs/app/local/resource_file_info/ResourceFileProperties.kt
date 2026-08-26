package kh.bank.dgb.ibs.app.local.resource_file_info

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Port of the old `PropertiesPlaceholderConfiguration.resourceUrl` (`${corp.banking.image.address}`)
 * — the externally-reachable base URL embedded in the `corporateUserProfileImageURL` sent to CBS
 * on upload, so CBS (or whatever downstream consumes that field) can fetch the image back later.
 *
 * Defaults to this app's own `/api/images/resources` endpoint (see `ResourceFileInfoCbc`); override
 * via `IBS_RESOURCE_IMAGE_BASE_URL` wherever the externally-reachable host/port differs from
 * localhost (e.g. behind the `/ibs` reverse-proxy prefix in SIT/UAT/PROD).
 */
@Component
@ConfigurationProperties(prefix = "ibs.resource")
data class ResourceFileProperties(
	var imageBaseUrl: String = "http://127.0.0.1:8080/ibs/api/images/resources",
)
