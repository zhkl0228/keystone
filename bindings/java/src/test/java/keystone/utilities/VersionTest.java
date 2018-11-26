/*
 * Copyright (c) 2018 Jämes Ménétrey <james@menetrey.me>
 *
 * This file is part of the Keystone Java bindings which is released under MIT.
 * See file LICENSE in the Java bindings folder for full license details.
 */

package keystone.utilities;

import junit.framework.TestCase;

public class VersionTest extends TestCase {

    private final int major = 3;
    private final int minor = 1;

    public void test_major_shouldReturnTheValueSpecifiedInTheConstructor() {
        // Arrange
        Version v = new Version(major, minor);

        // Act
        int m = v.major();

        // Assert
        assertEquals(major, m);
    }

    public void test_minor_shouldReturnTheValueSpecifiedInTheConstructor() {
        // Arrange
        Version v = new Version(major, minor);

        // Act
        int m = v.minor();

        // Assert
        assertEquals(minor, m);
    }

    public void test_compareTo_ifMajorIsNotEqual_shouldReturnDifferent() {
        // Arrange
        Version v1 = new Version(major, minor);
        Version v2 = new Version(major + 1, minor);

        // Act
        int lower = v1.compareTo(v2);
        int higher = v2.compareTo(v1);

        // Assert
        assertEquals(-1, lower);
        assertEquals(1, higher);
    }

    public void test_compareTo_ifMinorIsNotEqual_shouldReturnDifferent() {
        // Arrange
        Version v1 = new Version(major, minor + 1);
        Version v2 = new Version(major, minor);

        // Act
        int lower = v2.compareTo(v1);
        int higher = v1.compareTo(v2);

        // Assert
        assertEquals(-1, lower);
        assertEquals(1, higher);
    }
}