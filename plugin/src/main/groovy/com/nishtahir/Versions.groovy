package com.nishtahir

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.ImmutableSortedSet
import com.google.common.collect.Multimap
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.util.GradleVersion

@CompileStatic(TypeCheckingMode.SKIP)
class Versions {
    static final GradleVersion PLUGIN_VERSION
    static final Set<GradleVersion> SUPPORTED_GRADLE_VERSIONS
    static final Set<GradleVersion> SUPPORTED_ANDROID_VERSIONS
    static final Multimap<GradleVersion, GradleVersion> SUPPORTED_VERSIONS_MATRIX

    static {
        def versions = new JsonSlurper().parse(Versions.classLoader.getResource("versions.json"))

        PLUGIN_VERSION = GradleVersion.version(versions.version)

        def builder = ImmutableMultimap.<GradleVersion, GradleVersion> builder()
        versions.supportedVersions.each { String androidVersion, List<String> gradleVersions ->
            builder.putAll(android(androidVersion), gradleVersions.collect { gradle(it) })
        }
        def matrix = builder.build()

        SUPPORTED_VERSIONS_MATRIX = matrix
        SUPPORTED_ANDROID_VERSIONS = ImmutableSortedSet.copyOf(matrix.keySet())
        SUPPORTED_GRADLE_VERSIONS = ImmutableSortedSet.copyOf(matrix.values())
    }

    // 반환 타입 변경
    static GradleVersion android(String version) {
        GradleVersion.version(version)
    }

    static GradleVersion gradle(String version) {
        GradleVersion.version(version)
    }

    static GradleVersion earliestMaybeSupportedAndroidVersion() {
        GradleVersion earliestSupported = SUPPORTED_ANDROID_VERSIONS.min()

        def parts = earliestSupported.version.tokenize('.')
        if (parts.size() >= 2) {
            return GradleVersion.version("${parts[0]}.${parts[1]}.0-alpha")
        }

        return earliestSupported
    }

    static GradleVersion latestAndroidVersion() {
        return SUPPORTED_ANDROID_VERSIONS.max()
    }

    static int getMajor(GradleVersion v) {
        return getPart(v, 0)
    }

    static int getMinor(GradleVersion v) {
        return getPart(v, 1)
    }

    static int getPatch(GradleVersion v) {
        return getPart(v, 2)
    }

    private static int getPart(GradleVersion v, int index) {
        def parts = v.version.tokenize('.')
        if (index >= parts.size()) return 0
        def numberString = parts[index].find(/^\d+/)
        return numberString?.isInteger() ? numberString.toInteger() : 0
    }
}