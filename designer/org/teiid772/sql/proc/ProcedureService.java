/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.proc;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.query.IProcedureService;
import org.teiid.designer.query.proc.ITeiidColumnInfo;
import org.teiid.designer.query.proc.ITeiidMetadataFileInfo;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid772.runtime.Messages;

/**
 *
 */
public class ProcedureService implements IProcedureService, ISQLConstants {

    @Override
    public String getSQLStatement(ITeiidMetadataFileInfo metadataFileInfo, String relationalModelName) {
        /*
         * 
         * TEXTTABLE(expression COLUMNS <COLUMN>, ... [DELIMITER char] [(QUOTE|ESCAPE) char] [HEADER [integer]] [SKIP integer]) AS name
         * 
         * DELIMITER sets the field delimiter character to use. Defaults to ','.
         * 
         * QUOTE sets the quote, or qualifier, character used to wrap field values. Defaults to '"'.
         * 
         * ESCAPE sets the escape character to use if no quoting character is in use. This is used in situations where the delimiter or new line characters are escaped with a preceding character, e.g. \
         * 
         * 
            SELECT A.lastName, A.firstName, A.middleName, A.AId FROM
        (EXEC EmployeeData.getTextFiles('EmployeeData.txt')) AS f, TEXTTABLE(file COLUMNS lastName string, firstName string, middleName string, HEADER 3) AS A
         
         *
         * SELECT {0} FROM (EXEC {1}.getTextFiles({2})) AS f, TEXTTABLE(file COLUMNS {3}  HEADER {4}) AS {5}
         */
        List<String> tokens = new ArrayList<String>();
        List<ITeiidColumnInfo> columnInfoList = metadataFileInfo.getColumnInfoList();
        
        String alias = "A"; //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        int i=0;
        int nColumns = columnInfoList.size();
        for( ITeiidColumnInfo columnStr : columnInfoList) {
            sb.append(alias).append(DOT).append(columnStr.getSymbolName());
            
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }
            i++;
        }
        tokens.add(sb.toString());
        tokens.add(relationalModelName);
        
        sb = new StringBuffer();
        i=0;
        for( ITeiidColumnInfo columnStr : columnInfoList) {
            sb.append(columnStr.getSymbolName()).append(SPACE).append(columnStr.getDatatype());
            if( metadataFileInfo.isFixedWidthColumns()) {
                sb.append(SPACE).append(WIDTH).append(SPACE).append(Integer.toString(columnStr.getWidth()));
            }
            if(i < (nColumns-1)) {
                sb.append(COMMA).append(SPACE);
            }

            i++;
        }
        tokens.add(S_QUOTE + metadataFileInfo.getDataFile().getName() + S_QUOTE);
        tokens.add(sb.toString());
        
        sb = new StringBuffer();
        
        String delimiter = metadataFileInfo.getDelimiter();
        if( metadataFileInfo.doUseDelimitedColumns() && ! DEFAULT_DELIMITER.equals(delimiter) ) {
            sb.append("DELIMITER"); //$NON-NLS-1$
            sb.append(SPACE).append('\'').append(delimiter).append('\'');
        }
        
        String quote = metadataFileInfo.getQuote();
        
        if( metadataFileInfo.doIncludeQuote() ) {    
            if(! DEFAULT_QUOTE.equals(quote)) {
                sb.append("QUOTE"); //$NON-NLS-1$
                sb.append(SPACE).append('\'').append(quote).append('\'');
            }
        } else if(metadataFileInfo.doIncludeEscape() ) {
            String escape = metadataFileInfo.getEscape();
            if(! DEFAULT_ESCAPE.equals(escape)) {
                sb.append("ESCAPE"); //$NON-NLS-1$
                sb.append(SPACE).append('\'').append(escape).append('\'');
            }
        }
        
        if( metadataFileInfo.doIncludeHeader() ) {
            sb.append(SPACE).append("HEADER"); //$NON-NLS-1$
            if( metadataFileInfo.getHeaderLineNumber() > 1 ) {
                sb.append(SPACE).append(Integer.toString(metadataFileInfo.getHeaderLineNumber()));
            }
        }
        
        int firstDataRow = metadataFileInfo.getFirstDataRow();
        
        if( firstDataRow > 1 && (metadataFileInfo.doIncludeSkip() || metadataFileInfo.isFixedWidthColumns()) ) {
            sb.append(SPACE).append("SKIP"); //$NON-NLS-1$
            sb.append(SPACE).append(Integer.toString(firstDataRow-1));
        }
        
        if( metadataFileInfo.doIncludeNoTrim() && firstDataRow > 1 ) {
            sb.append(SPACE).append("NO TRIM"); //$NON-NLS-1$
        }
        tokens.add(sb.toString());
        tokens.add(alias);
        
        String finalSQLString = NLS.bind(Messages.procedureServiceTextTableSqlTemplate, tokens.toArray(new String[0]));
        return finalSQLString;
    }

}
