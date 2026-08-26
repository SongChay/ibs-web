package kh.bank.dgb.ibs.app.local.file_download

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Port of `FileController.handleFileDownload` (`GET /download?filePath=...`) and
 * `DownloadController.doDownload` (`GET /download/{filename}`) — see `FileDownloadSbc` for the
 * real logic and the security note on what was deliberately NOT ported byte-for-byte.
 *
 * No route collision with `/download/manual/{resID}` or `/download/attachment/{id}` (registered
 * elsewhere) — Spring ranks the more specific literal-prefixed mappings above this catch-all, same
 * disambiguation the old app itself relied on (both lived in the same old `DownloadController`).
 */
@RestController
class FileDownloadCbc(
	private val fileDownloadSbc: FileDownloadSbc,
) {
	@GetMapping("/download")
	fun downloadByFilePath(@RequestParam filePath: String): ResponseEntity<ByteArray> {
		return fileDownloadSbc.downloadByFilePath(filePath)
	}

	@GetMapping("/download/{filename:.+}")
	fun downloadByFilename(@PathVariable filename: String): ResponseEntity<ByteArray> {
		return fileDownloadSbc.downloadByFilename(filename)
	}
}
