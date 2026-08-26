plugins {
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "2.1.10"
	kotlin("plugin.spring") version "2.1.10"
}

group = "kh.bank.dgb"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	// spring-session-data-redis alone has no RedisConnectionFactory bean to attach to — that comes
	// from spring-boot-starter-data-redis. Without it, Spring Session's Redis autoconfiguration
	// never activates and silently falls back to plain Tomcat in-memory sessions (confirmed the
	// hard way: login/session appeared to work fine, but Redis stayed completely empty — same
	// modularization pattern as the Flyway and MyBatis starters above).
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-session-data-redis")
	implementation("org.springframework.session:spring-session-data-redis")
	// 3.0.x only supports Spring Boot 3.2-3.5 (confirmed the hard way — its MybatisAutoConfiguration
	// doesn't order correctly after DataSourceAutoConfiguration under Boot 4's autoconfiguration
	// processing, leaving mapper beans with no SqlSessionFactory). 4.x is the Boot-4-compatible line.
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	// Boot 4's default JSON engine is Jackson 3 (tools.jackson.*), not Jackson 2 — the Kotlin
	// module for it lives under a new groupId/artifact, not com.fasterxml.jackson.module.
	implementation("tools.jackson.module:jackson-module-kotlin:3.1.5")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// Boot 4 modularized autoconfiguration further than expected: flyway-core/-database-postgresql
	// alone no longer trigger migration-on-startup (confirmed the hard way — silently did nothing,
	// no log line, no error). spring-boot-starter-flyway is the actual auto-configuration trigger.
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.flywaydb:flyway-database-postgresql")
	// QR code generation for /USR2001 and /generateQrCode/{userID}.png — the old app's
	// com.google.zxing:core + :javase, added here so those two stubbed-null endpoints can finally
	// do the real encoding instead of leaving qrCodeUrl/the image body empty.
	implementation("com.google.zxing:core:3.5.3")
	implementation("com.google.zxing:javase:3.5.3")
	runtimeOnly("org.postgresql:postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter:1.21.3")
	testImplementation("org.testcontainers:postgresql:1.21.3")
}

kotlin {
	compilerOptions {
		// -java-parameters: MyBatis resolves #{paramName} in mapper XML against real method
		// parameter names, which the JVM strips by default — without this flag every mapper
		// method needs an explicit @Param annotation even where the name already matches.
		freeCompilerArgs.addAll("-Xjsr305=strict", "-java-parameters")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
