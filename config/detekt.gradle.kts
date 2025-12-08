plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules
    config.setFrom("$projectDir/config/detekt.yml") // point to your custom config
    baseline = file("$projectDir/config/detekt-baseline.xml") // suppress existing issues
    
    source.setFrom(
        "src/main/java",
        "src/main/kotlin",
        "src/test/java",
        "src/test/kotlin"
    )
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true) // HTML report
        xml.required.set(true) // Checkstyle compatible XML
        txt.required.set(true) // Simple text report
        sarif.required.set(true) // SARIF for GitHub
        md.required.set(true) // Markdown report
    }
}
