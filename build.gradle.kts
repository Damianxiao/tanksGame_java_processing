plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "tt"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(fileTree("libs") { include("*.jar") })
}

application {
    mainClass.set("tt.Main")
}

tasks.test {
    useJUnitPlatform()
}
