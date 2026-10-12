plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

group = "com.vanmors.lsh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("it.unimi.dsi:fastutil-core:8.5.15")
}

tasks.test {
    useJUnitPlatform()
}

val asyncLib = "/Users/imorikov/Education/masters/Structures-and-algorithms-in-databases-and-distributed-systems/async-profiler/lib/libasyncProfiler.dylib"



jmh {
    jmhVersion.set("1.37")
    warmupIterations.set(3)
    iterations.set(5)
    fork.set(1)
    profilers.set(listOf(
        "gc",
        "async:libPath=$asyncLib;event=cpu;output=flamegraph;dir=build/async"
    ))
}
