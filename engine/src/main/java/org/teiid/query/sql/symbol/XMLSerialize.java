/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.symbol;

import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.query.sql.symbol.IXMLSerialize;
import org.teiid.query.sql.LanguageVisitor;
import org.teiid.query.sql.visitor.SQLStringVisitor;

public class XMLSerialize implements Expression, IXMLSerialize<LanguageVisitor> {

	private static final long serialVersionUID = -6574662238317329252L;
	
	private Boolean document;
	private Expression expression;
	private String typeString;
	private Class<?> type;
	
	@Override
	public Class<?> getType() {
		if (type == null) {
			if (typeString == null) {
				type = DataTypeManager.DefaultDataClasses.CLOB;
			} else {
				type = DataTypeManager.getDataTypeClass(typeString);
			}
		}
		return type;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Boolean isDocument() {
		return document;
	}
	
	public void setDocument(Boolean document) {
		this.document = document;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	
	public String getTypeString() {
		return typeString;
	}
	
	@Override
	public boolean isResolved() {
		return expression.isResolved();
	}
	
	@Override
	public void acceptVisitor(LanguageVisitor visitor) {
		visitor.visit(this);
	}
		
	@Override
	public XMLSerialize clone() {
		XMLSerialize clone = new XMLSerialize();
		clone.document = this.document;
		clone.expression = (Expression)this.expression.clone();
		clone.typeString = this.typeString;
		clone.type = this.type;
		return clone;
	}
	
	@Override
	public int hashCode() {
		return expression.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof XMLSerialize)) {
			return false;
		}
		XMLSerialize other = (XMLSerialize)obj;
		return document == other.document 
			&& this.expression.equals(other.expression)
			&& this.getType() == other.getType();
	}
	
	@Override
	public String toString() {
		return SQLStringVisitor.getSQLString(this);
	}
	
}
