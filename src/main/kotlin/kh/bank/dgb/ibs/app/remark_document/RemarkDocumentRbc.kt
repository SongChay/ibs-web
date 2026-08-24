package kh.bank.dgb.ibs.app.remark_document

import org.apache.ibatis.annotations.Mapper

/** Port of `RemarkDocumentDTO` — code/item lookup table, same shape as `PrepareDocument` but a
 *  distinct table/domain in the old app. */
data class RemarkDocument(
	val code: String,
	val parentCode: String? = null,
	val item: String? = null,
)

/** Port of `RemarkDocumentDAO`. */
@Mapper
interface RemarkDocumentRbc {
	fun getRemarkDocumentList(parentCode: String): List<RemarkDocument>
}
