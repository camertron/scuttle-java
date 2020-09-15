package com.camertron.Scuttle;

public class ScuttleOptions {
    protected static SemVer DEFAULT_RAILS_VERSION = SemVer.fromString("6.0.0");

    protected boolean m_bUseArelNodesPrefix = true;
    protected boolean m_bUseArelHelpers = false;
    protected SemVer m_svRailsVersion;

    public void useArelNodesPrefix(boolean value) {
        m_bUseArelNodesPrefix = value;
    }

    public boolean shouldUseArelNodesPrefix() {
        return m_bUseArelNodesPrefix;
    }

    public void useArelHelpers(boolean value) {
        m_bUseArelHelpers = value;
    }

    public boolean shouldUseArelHelpers() {
        return m_bUseArelHelpers;
    }

    public void useRailsVersion(String sRailsVersion) {
        m_svRailsVersion = SemVer.fromString(sRailsVersion);
    }

    public SemVer getRailsVersion() {
        if (m_svRailsVersion != null) {
            return m_svRailsVersion;
        }

        return DEFAULT_RAILS_VERSION;
    }

    public String namespaceArelNodeClass(String base) {
        if (shouldUseArelNodesPrefix()) {
            return "Arel::Nodes::" + base;
        } else {
            return base;
        }
    }

    public String formatArelColumn(String sTableClass, String sColumnName) {
        if (shouldUseArelHelpers()) {
            return sTableClass + "[" + sColumnName + "]";
        } else {
            return sTableClass + ".arel_table[" + sColumnName + "]";
        }
    }
}
