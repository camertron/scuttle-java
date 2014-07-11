package com.camertron.Scuttle.Resolver;

public class AssociationMetadata {
  private AssociationType m_type;
  private String m_sForeignKey;
  private String m_sAssocName;

  public AssociationMetadata(AssociationType type, String sAssocName, String sForeignKey) {
    m_type = type;
    m_sForeignKey = sForeignKey;
    m_sAssocName = sAssocName;
  }

  public AssociationType getType() {
    return m_type;
  }

  public String getForeignKey() {
    return m_sForeignKey;
  }

  public String getAssociationName() {
    return m_sAssocName;
  }
}
