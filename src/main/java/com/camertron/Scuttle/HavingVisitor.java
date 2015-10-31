package com.camertron.Scuttle;

import com.camertron.SQLParser.SQLParser;
import com.camertron.Scuttle.Resolver.AssociationResolver;
import org.antlr.v4.runtime.misc.NotNull;

public class HavingVisitor extends ScuttleBaseVisitor {
    String m_sExpression = "";

    public HavingVisitor(FromVisitor fmFromVisitor, AssociationResolver arResolver, ScuttleOptions sptOptions) {
        super(fmFromVisitor, arResolver, sptOptions);
    }

    @Override public Void visitBoolean_value_expression(@NotNull SQLParser.Boolean_value_expressionContext ctx) {
        ValueExpressionVisitor veVisitor = new ValueExpressionVisitor(m_fmFromVisitor, m_arResolver, m_sptOptions);
        veVisitor.visit(ctx);
        m_sExpression = ExpressionUtils.formatOperand(veVisitor.toString(), false, m_sptOptions);
        return null;
    }

    public String toString() {
        return m_sExpression;
    }
}
