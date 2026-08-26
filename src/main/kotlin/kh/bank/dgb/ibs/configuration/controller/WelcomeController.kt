package kh.bank.dgb.ibs.configuration.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Port of `WelcomeController` — two trivial, whitelisted endpoints that were never actually wired
 * up in this port.
 *  - `/` was a plain banner string built from `set.mode` (`"LOCAL - New SmartBiz Server works~!"`);
 *    this app has no equivalent per-environment mode flag, so a generic string stands in for that
 *    half.
 *  - `/getCurrentHHMM` returns the server's current time as `HHmm`, same format as the old
 *    `DateUtil.getCurrentHHMM()`.
 */
@RestController
class WelcomeController {
	@GetMapping("/")
	fun welcome(): String = "ibs-web - New SmartBiz Server works~!"

	@GetMapping("/getCurrentHHMM")
	fun currentHHMM(): String = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm"))
}
