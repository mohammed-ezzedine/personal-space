plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "me.ezzedine.mohammed"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()

	finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("runUnitTests") {
	filter {
		excludeTestsMatching("*IntegrationTest")
		isFailOnNoMatchingTests = false
	}
}

tasks.register<Test>("runIntegrationTests") {
	filter {
		includeTestsMatching("*IntegrationTest")
		isFailOnNoMatchingTests = false
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.named("runUnitTests"), tasks.named("runIntegrationTests")) // tests are required to run before generating the report
}

jacoco {
	reportsDirectory = file("${layout.buildDirectory}/reports/jacoco")
}