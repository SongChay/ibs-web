package kh.bank.dgb.ibs.cbs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Port of the Maru-protocol-relevant fields from the old `PropertiesPlaceholderConfiguration` /
 * `server-config.properties`. Non-Maru fields from that old class (RSA server URL, image/resource
 * URLs, MCI file-upload URL, session timeout) belong to other features, not this connector —
 * ported separately if/when those features need them.
 *
 * Defaults below match the old app's local/dev `server-config.properties` (`set.mode=LOCAL`), so
 * this runs locally with no configuration — every value is still overridable via env var
 * (`IBS_CBS_*`) for SIT/UAT/PROD, replacing the old app's manual per-environment file-copy
 * convention.
 */
@Component
@ConfigurationProperties(prefix = "ibs.cbs")
data class CoreBankingProperties(
	var baseUrl: String = "http://127.0.0.1:10210/ONLWeb/HttpCsbSyncAdapter",
	var companyIdCode: String = "DGB",
	var systemCode: String = "CSB",
	var messageVersionNumber: String = "D09",
	var totalTimeoutSeconds: String = "120",
	var transactionTimeoutSeconds: String = "120",
	var countryCode: String = "116",
	var transactionSyncTypeCode: String = "S",
	var systemEnvironmentTypeCode: String = "D",
	var transactionProcessTypeCode: String = "O",
	var originalTransactionRestoreYn: String = "N",
	var bankCode: String = "DGB",
	var transactionBranchCode: String = "0901",
	var channelTypeCode: String = "I",
	var channelDetailTypeCode: String = "CIB",
	var terminalNumber: String = "000000",
	var defaultLanguageTypeCode: String = "EN",
	var tellerId: String = "700002",
	var actualTransactionBranchCode: String = "0704",
)
