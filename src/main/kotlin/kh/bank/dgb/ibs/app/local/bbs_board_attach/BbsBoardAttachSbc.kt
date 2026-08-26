package kh.bank.dgb.ibs.app.local.bbs_board_attach

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

/**
 * Port of `DownloadController.downloadById` (`GET /download/attachment/{id}`) — serves a BBS board
 * attachment's raw bytes (e.g. a downloadable PDF on a news/FAQ post). Only the DAO existed until
 * now; nothing ever wired it into an actual endpoint. Old code silently did nothing on a missing
 * attachment (200, empty body, no content-type) — kept exactly, matching the same convention used
 * for the other byte-serving endpoints in this app.
 */
@Service
class BbsBoardAttachSbc(
	private val bbsBoardAttachRbc: BbsBoardAttachRbc,
) {
	fun downloadAttachment(id: Int): ResponseEntity<ByteArray> {
		val attach = bbsBoardAttachRbc.getById(id)
		val data = attach?.attachData ?: return ResponseEntity.ok().build()

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, attach.attachContentType ?: "application/octet-stream")
			.body(data)
	}
}
