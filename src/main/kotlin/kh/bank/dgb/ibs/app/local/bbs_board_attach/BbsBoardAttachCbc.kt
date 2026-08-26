package kh.bank.dgb.ibs.app.local.bbs_board_attach

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/** Port of `DownloadController.downloadById` — see `BbsBoardAttachSbc` for the real logic. */
@RestController
class BbsBoardAttachCbc(
	private val sbc: BbsBoardAttachSbc,
) {
	@GetMapping("/download/attachment/{id}")
	fun downloadAttachment(@PathVariable id: Int): ResponseEntity<ByteArray> =
		sbc.downloadAttachment(id)
}
