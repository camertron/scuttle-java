package com.camertron.Scuttle;

public class ScuttleOptions {
    protected boolean m_bUseArelNodesPrefix = true;
    protected boolean m_bUseArelHelpers = false;

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
