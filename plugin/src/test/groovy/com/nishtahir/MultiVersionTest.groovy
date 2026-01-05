package com.nishtahir

import java.lang.annotation.*

/**
 * Represents tests that span multiple versions of Android Gradle Plugin and need to be executed
 * with multiple versions of the JDK.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface MultiVersionTest {}
