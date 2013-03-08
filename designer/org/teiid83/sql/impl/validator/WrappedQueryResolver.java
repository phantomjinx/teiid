/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid83.sql.impl.validator;

import java.util.List;
import org.teiid.designer.query.IQueryResolver;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid83.sql.impl.CrossQueryMetadata;

/**
 *
 */
public class WrappedQueryResolver implements IQueryResolver<Command, GroupSymbol> {
    
    @Override
    public void resolveCommand(Command command, GroupSymbol gSymbol, int commandType, 
                               IQueryMetadataInterface metadata) throws Exception {
        
        CrossQueryMetadata cqMetadata = new CrossQueryMetadata(metadata);
        QueryResolver.resolveCommand(command, gSymbol, commandType, cqMetadata, true);
    }

    @Override
    public void postResolveCommand(Command command, GroupSymbol gSymbol, int commandType,
                                   IQueryMetadataInterface metadata, List<IElementSymbol> projectedSymbols) {
        // Nothing required
    }

}
