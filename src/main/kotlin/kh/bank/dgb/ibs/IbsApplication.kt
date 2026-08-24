package kh.bank.dgb.ibs

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("kh.bank.dgb.ibs.app")
class IbsApplication

fun main(args: Array<String>) {
	runApplication<IbsApplication>(*args)
}
