package kh.bank.dgb.ibs.app.bbs_board_attach

import org.apache.ibatis.annotations.Mapper
import java.time.LocalDateTime

/** Port of `BbsBoardAttachDTO` (bizmob.corpbanking.dto). */
data class BbsBoardAttach(
	val attachId: Int,
	val boardId: Int,
	val attachName: String? = null,
	val attachOriginalName: String? = null,
	val attachExt: String? = null,
	val attachSize: Double? = null,
	val attachContentType: String? = null,
	val attachData: ByteArray? = null,
	val createdBy: String? = null,
	val createdDate: LocalDateTime? = null,
	val isDeleted: Int? = null,
	val deletedBy: String? = null,
	val deletedDate: LocalDateTime? = null,
)

/** Port of `BbsBoardAttachDAO` — file attachments on a `bbs_board` entry (e.g. a downloadable PDF
 *  on a news/FAQ post). `getById` returns the blob (`attachData`), `getAttachByBoardId` doesn't
 *  — same distinction the old two-resultMap XML made, kept via which columns each query selects. */
@Mapper
interface BbsBoardAttachRbc {
	fun getAttachByBoardId(boardId: Int): List<BbsBoardAttach>
	fun getById(id: Int): BbsBoardAttach?
}
