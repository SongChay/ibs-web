package kh.bank.dgb.ibs.app.cbs.generate_qr_code

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

/**
 * Shared QR-PNG encoder for the old app's two independent QR endpoints, both of which zxing-encode
 * the same CBS-derived `otpAuthString` (an `otpauth://` TOTP URI) — just packaged differently:
 *  - `USR2001_Adapter_GenerateQRCode` embeds the PNG as a base64 data URL inside JSON (142x142).
 *  - `QRCodeController` returns the PNG as a raw image response (200x200).
 *
 * One encoder, two callers, two different sizes — kept faithful to each old endpoint's own size
 * rather than unifying them.
 */
object QrCodeEncoder {
	private const val BLACK = 0xFF000000.toInt()
	private const val WHITE = 0xFFFFFFFF.toInt()

	fun encodePng(content: String, size: Int): ByteArray {
		val hints = mapOf(
			EncodeHintType.MARGIN to "0",
			EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
		)
		val matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
		val image = MatrixToImageWriter.toBufferedImage(matrix, MatrixToImageConfig(BLACK, WHITE))
		return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
	}

	fun encodePngBase64DataUrl(content: String, size: Int): String =
		"data:image/png;base64,${Base64.getEncoder().encodeToString(encodePng(content, size))}"
}
