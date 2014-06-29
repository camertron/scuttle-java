package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParserBaseVisitor;
import com.camertron.Scuttle.Resolver.AssociationResolver;

public class ScuttleBaseVisitor extends SQLParserBaseVisitor<Void> {
  protected FromVisitor m_fmFromVisitor;
  protected AssociationResolver m_arResolver;

  public ScuttleBaseVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver) {
    m_fmFromVisitor = fmFromVisitor;
    m_arResolver = arResolver;
  }
}
