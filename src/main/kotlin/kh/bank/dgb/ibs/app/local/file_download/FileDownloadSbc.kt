package kh.bank.dgb.ibs.app.local.file_download

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Port of `FileController.handleFileDownload` (`GET /download?filePath=...`) and
 * `DownloadController.doDownload` (`GET /download/{filename}`) — both serve an arbitrary file by
 * relative path/name from a fixed base directory.
 *
 * SECURITY NOTE, explicitly flagged rather than silently ported: the old code applied ZERO path
 * validation — `filePath`/`filename` were concatenated directly into a `File` path with no check
 * that the result stayed inside the intended directory. That's an unrestricted arbitrary-file-read
 * vulnerability (`../../../../etc/passwd`-style traversal, or an absolute path outright, both work
 * against the original). Built here to actually function, per explicit instruction to get this
 * working now and revisit it with the client — but NOT a byte-for-byte port of that omission:
 * every resolved path is normalized and checked to still be inside
 * [FileDownloadProperties.docsBasePath] before being served, rejecting `..` escapes and absolute
 * paths outright. This is the smallest possible guard that doesn't change behavior for any
 * legitimate caller (nothing legitimate should ever need to escape the configured directory), so
 * "make it work" and "don't hand out arbitrary filesystem access" aren't actually in tension here.
 * Further hardening (e.g. an allowlist of permitted file IDs instead of raw filesystem paths,
 * matching how `/download/manual/{resID}` and `/download/attachment/{id}` already work) is exactly
 * the kind of thing worth raising with the client.
 */
@Service
class FileDownloadSbc(
	private val fileDownloadProperties: FileDownloadProperties,
) {
	private val logger = LoggerFactory.getLogger(FileDownloadSbc::class.java)

	/** Port of `FileController.handleFileDownload` — old code returned `204 No Content` (not a
	 *  silent-empty-200 like the other download endpoints in this app) when the file didn't exist
	 *  or was a directory; kept exactly. */
	fun downloadByFilePath(filePath: String): ResponseEntity<ByteArray> {
		val resolved = resolveWithinBase(filePath)
		if (resolved == null || !Files.exists(resolved) || Files.isDirectory(resolved)) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
		}

		val encodedName = UriUtils.encode(resolved.fileName.toString(), StandardCharsets.UTF_8)
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, "application/octet-stream; charset=UTF-8")
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$encodedName")
			.body(Files.readAllBytes(resolved))
	}

	/** Port of `DownloadController.doDownload` — old code silently did nothing on a missing file
	 *  (200, empty body, no content-type), matching every other byte-serving endpoint in this app;
	 *  kept exactly rather than switching to 204/404. */
	fun downloadByFilename(filename: String): ResponseEntity<ByteArray> {
		val resolved = resolveWithinBase(filename)
		if (resolved == null || !Files.exists(resolved) || Files.isDirectory(resolved)) {
			return ResponseEntity.ok().build()
		}

		val mimeType = runCatching { Files.probeContentType(resolved) }.getOrNull() ?: "application/octet-stream"
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, mimeType)
			.body(Files.readAllBytes(resolved))
	}

	/** Resolves [path] against the configured base directory and verifies the normalized result is
	 *  still inside it. Returns null for anything that would escape (`..` traversal, an absolute
	 *  path overriding the base entirely, etc.) — callers map that to whichever "not found" shape
	 *  their old endpoint used. */
	private fun resolveWithinBase(path: String): Path? {
		val base = Path.of(fileDownloadProperties.docsBasePath).toAbsolutePath().normalize()
		val candidate = base.resolve(path).normalize()

		if (!candidate.startsWith(base)) {
			logger.warn("Rejected file-download path escaping base directory: {}", path)
			return null
		}
		return candidate
	}
}
