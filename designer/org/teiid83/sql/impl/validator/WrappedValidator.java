/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid83.sql.impl.validator;

import org.teiid.core.TeiidComponentException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.validator.IValidator;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.validator.Validator;
import org.teiid.query.validator.ValidatorReport;
import org.teiid83.sql.impl.CrossQueryMetadata;

/**
 *
 */
public class WrappedValidator implements IValidator<LanguageObject> {
    
    @Override
    public IValidatorReport validate(LanguageObject languageObject, IQueryMetadataInterface queryMetadata) throws Exception {
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(queryMetadata);
        
        ValidatorReport validateReport;
        try {
            validateReport = Validator.validate(languageObject, dMetadata);
            return new WrappedValidatorReport(validateReport);
        } catch (TeiidComponentException ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
