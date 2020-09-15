package com.camertron.Scuttle;

import javax.management.RuntimeErrorException;

public class SemVer implements Comparable<Object> {
    private final int major;
    private final int minor;
    private final int patch;

    public static SemVer fromString(String ver) {
        String[] parts = ver.split("\\.");
        return new SemVer(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    public SemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int compareTo(Object o) {
        if (o instanceof String) {
            return compareTo(SemVer.fromString((String)o));
        } else if (o instanceof SemVer) {
            SemVer other = (SemVer)o;

            if (major == other.major) {
                if (minor == other.minor) {
                    if (patch == other.patch) {
                        return 0;
                    } else {
                        return Integer.compare(patch, other.patch);
                    }
                } else {
                    return Integer.compare(minor, other.minor);
                }
            } else {
                return Integer.compare(major, other.major);
            }
        } else {
            throw new RuntimeException("Could not compare objects");
        }
    }

    public boolean greaterThan(Object ver) {
        return compareTo(ver) > 0;
    }

    public boolean greaterThanOrEqualTo(Object ver) {
        return compareTo(ver) >= 0;
    }

    public boolean lessThan(Object ver) {
        return compareTo(ver) < 0;
    }

    public boolean lessThanOrEqualTo(Object ver) {
        return compareTo(ver) <= 0;
    }

    public boolean equals(Object ver) {
        return compareTo(ver) == 0;
    }
}
