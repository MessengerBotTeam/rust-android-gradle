package com.nishtahir

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import org.gradle.util.GradleVersion


class TestVersions {

    static Multimap<GradleVersion, GradleVersion> getAllCandidateTestVersions() {
        def testedVersion = System.getProperty('org.gradle.android.testVersion')
        if (testedVersion) {
            return ImmutableMultimap.copyOf(Versions.SUPPORTED_VERSIONS_MATRIX.entries().findAll { it.key == GradleVersion.version(testedVersion) })
        } else {
            return Versions.SUPPORTED_VERSIONS_MATRIX
        }
    }

    static GradleVersion latestAndroidVersionForCurrentJDK() {
        String currentJDKVersion = System.getProperty("java.version")
        if (currentJDKVersion.startsWith("1.")) {
            return allCandidateTestVersions.keySet().findAll { it < GradleVersion.version("7.0.0-alpha01") }.max()
        }
        return allCandidateTestVersions.keySet().max()
    }

    static GradleVersion latestGradleVersion() {
        return allCandidateTestVersions.values().max()
    }

    static GradleVersion latestSupportedGradleVersionFor(String androidVersion) {
        return latestSupportedGradleVersionFor(GradleVersion.version(androidVersion))
    }

    static GradleVersion latestSupportedGradleVersionFor(GradleVersion androidVersion) {
        return allCandidateTestVersions.asMap().find {
            getMajor(it.key) == getMajor(androidVersion) && getMinor(it.key) == getMinor(androidVersion)
        }?.value?.max()
    }

    static GradleVersion getLatestVersionForAndroid(String version) {
        GradleVersion versionNumber = GradleVersion.version(version)
        return allCandidateTestVersions.keySet().findAll {
            getMajor(it) == getMajor(versionNumber) && getMinor(it) == getMinor(versionNumber)
        }?.max()
    }

    static List<GradleVersion> getLatestAndroidVersions() {
        def minorVersions = allCandidateTestVersions.keySet().collect { "${getMajor(it)}.${getMinor(it)}" }
        return minorVersions.collect { getLatestVersionForAndroid(it) }
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
