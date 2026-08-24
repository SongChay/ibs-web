package kh.bank.dgb.ibs.app.resource_file_info

import org.apache.ibatis.annotations.Mapper
import java.time.LocalDateTime

/** Port of `ResourceFileInfoDTO` (persistence-relevant fields only — the old DTO also carried a
 *  `MultipartFile`/URL-building fields that belong to the upload-handling layer, not storage). */
data class ResourceFileInfo(
	val id: String,
	val fileTypeCode: String? = null,
	val fileName: String? = null,
	val fileExt: String? = null,
	val fileContentType: String? = null,
	val fileSize: Long? = null,
	val fileData: ByteArray? = null,
	val createdBy: String? = null,
	val createdDate: LocalDateTime? = null,
	val updatedBy: String? = null,
	val updatedDate: LocalDateTime? = null,
)

/** Port of `RescourceFileInfoDAO` (typo fixed — old class/table were misspelled "Rescource").
 *  `addCompanyProfile`'s column list intentionally omits `created_date` on insert, matching the
 *  old app exactly (whether that's a real bug or intentional wasn't ours to decide here). */
@Mapper
interface ResourceFileInfoRbc {
	fun getLastId(): Long
	fun addCompanyProfile(resource: ResourceFileInfo): Int
	fun updateCompanyProfile(resource: ResourceFileInfo): Int
	fun getResourceById(id: String): ResourceFileInfo?
}
