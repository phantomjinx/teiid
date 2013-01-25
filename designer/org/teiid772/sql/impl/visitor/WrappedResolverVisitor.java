/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.Collection;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.IResolverVisitor;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid772.sql.impl.CrossQueryMetadata;

/**
 *
 */
public class WrappedResolverVisitor
    implements IResolverVisitor<LanguageObject, GroupSymbol> {

    @Override
    public void setProperty(String propertyName, Object value) {
        if (SHORT_NAME.equals(propertyName) && value instanceof Boolean)
            ResolverVisitor.setFindShortName((Boolean) value);
    }

    @Override
    public void resolveLanguageObject(LanguageObject obj, IQueryMetadataInterface metadata) throws Exception {
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(metadata);
        ResolverVisitor.resolveLanguageObject(obj, dMetadata);
    }

    @Override
    public void resolveLanguageObject(LanguageObject obj, Collection<GroupSymbol> groups, IQueryMetadataInterface metadata)
        throws Exception {
        
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(metadata);
        ResolverVisitor.resolveLanguageObject(obj, groups, dMetadata);
    }

}
