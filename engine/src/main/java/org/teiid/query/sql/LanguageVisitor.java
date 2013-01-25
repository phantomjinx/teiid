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

package org.teiid.query.sql;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IAlterProcedure;
import org.teiid.designer.query.sql.lang.IAlterTrigger;
import org.teiid.designer.query.sql.lang.IAlterView;
import org.teiid.designer.query.sql.lang.IArrayTable;
import org.teiid.designer.query.sql.lang.IBatchedUpdateCommand;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICreate;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IDependentSetCriteria;
import org.teiid.designer.query.sql.lang.IDrop;
import org.teiid.designer.query.sql.lang.IDynamicCommand;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IExpressionCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IJoinType;
import org.teiid.designer.query.sql.lang.ILimit;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IObjectTable;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.designer.query.sql.lang.IProcedureContainer;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetClause;
import org.teiid.designer.query.sql.lang.ISetClauseList;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.ITextTable;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.lang.IWithQueryCommand;
import org.teiid.designer.query.sql.lang.IXMLTable;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.IBranchingStatement;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.ICriteriaSelector;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IExceptionExpression;
import org.teiid.designer.query.sql.proc.IHasCriteria;
import org.teiid.designer.query.sql.proc.IIfStatement;
import org.teiid.designer.query.sql.proc.ILoopStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.proc.IReturnStatement;
import org.teiid.designer.query.sql.proc.ITranslateCriteria;
import org.teiid.designer.query.sql.proc.ITriggerAction;
import org.teiid.designer.query.sql.proc.IWhileStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IArray;
import org.teiid.designer.query.sql.symbol.ICaseExpression;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IDerivedColumn;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IQueryString;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.designer.query.sql.symbol.ISearchedCaseExpression;
import org.teiid.designer.query.sql.symbol.ITextLine;
import org.teiid.designer.query.sql.symbol.IWindowFunction;
import org.teiid.designer.query.sql.symbol.IWindowSpecification;
import org.teiid.designer.query.sql.symbol.IXMLAttributes;
import org.teiid.designer.query.sql.symbol.IXMLElement;
import org.teiid.designer.query.sql.symbol.IXMLForest;
import org.teiid.designer.query.sql.symbol.IXMLNamespaces;
import org.teiid.designer.query.sql.symbol.IXMLParse;
import org.teiid.designer.query.sql.symbol.IXMLQuery;
import org.teiid.designer.query.sql.symbol.IXMLSerialize;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BatchedUpdateCommand;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.DependentSetCriteria;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.CriteriaSelector;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.HasCriteria;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.TranslateCriteria;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;

/**
 * <p>The LanguageVisitor can be used to visit a LanguageObject as if it were a tree
 * and perform some action on some or all of the language objects that are visited.
 * The LanguageVisitor is extended to create a concrete visitor and some or all of 
 * the public visit methods should be overridden to provide the visitor functionality. 
 * These public visit methods SHOULD NOT be called directly.</p>
 */
@SuppressWarnings("unused")
public abstract class LanguageVisitor implements ILanguageVisitor {
    
    private boolean abort = false;

    public void setAbort(boolean abort) {
        this.abort = abort;
    }
    
    public final boolean shouldAbort() {
        return abort;
    }
   
    // Visitor methods for language objects
    public void visit(BatchedUpdateCommand obj) {}
    public void visit(BetweenCriteria obj) {}
    public void visit(CaseExpression obj) {}
    public void visit(CompareCriteria obj) {}
    public void visit(CompoundCriteria obj) {}
    public void visit(Delete obj) {
        visit((ProcedureContainer)obj);
    }
    public void visit(ExistsCriteria obj) {}
    public void visit(From obj) {}
    public void visit(GroupBy obj) {}
    public void visit(Insert obj) {
        visit((ProcedureContainer)obj);
    }
    public void visit(IsNullCriteria obj) {}
    public void visit(JoinPredicate obj) {}
    public void visit(JoinType obj) {}
    public void visit(Limit obj) {}
    public void visit(MatchCriteria obj) {}
    public void visit(NotCriteria obj) {}
    public void visit(Option obj) {}
    public void visit(OrderBy obj) {}
    public void visit(Query obj) {}
    public void visit(SearchedCaseExpression obj) {}
    public void visit(Select obj) {}
    public void visit(SetCriteria obj) {}
    public void visit(SetQuery obj) {}
    public void visit(StoredProcedure obj) {
        visit((ProcedureContainer)obj);
    }
    public void visit(SubqueryCompareCriteria obj) {}
    public void visit(SubqueryFromClause obj) {}
    public void visit(SubquerySetCriteria obj) {}
    public void visit(UnaryFromClause obj) {}
    public void visit(Update obj) {
        visit((ProcedureContainer)obj);
    }
    public void visit(Into obj) {}
    public void visit(DependentSetCriteria obj) {}
    public void visit(Create obj) {}
    public void visit(Drop obj) {}

    // Visitor methods for symbol objects
    public void visit(AggregateSymbol obj) {}
    public void visit(AliasSymbol obj) {}
    public void visit(MultipleElementSymbol obj) {}
    public void visit(Constant obj) {}
    public void visit(ElementSymbol obj) {}
    public void visit(ExpressionSymbol obj) {}
    public void visit(Function obj) {}
    public void visit(GroupSymbol obj) {}
    public void visit(Reference obj) {}
    public void visit(ScalarSubquery obj) {}
    
    // Visitor methods for procedure language objects    
    public void visit(AssignmentStatement obj) {}
    public void visit(Block obj) {}
    public void visit(CommandStatement obj) {}
    public void visit(CreateUpdateProcedureCommand obj) {}
    public void visit(CriteriaSelector obj) {}
    public void visit(DeclareStatement obj) {
        visit((AssignmentStatement)obj);
    }    
    public void visit(HasCriteria obj) {}
    public void visit(IfStatement obj) {}
    public void visit(RaiseErrorStatement obj) {}
    public void visit(TranslateCriteria obj) {}
    public void visit(BranchingStatement obj) {}
    public void visit(WhileStatement obj) {}
    public void visit(LoopStatement obj) {}
    public void visit(DynamicCommand obj) {}
    public void visit(ProcedureContainer obj) {}
    public void visit(SetClauseList obj) {}
    public void visit(SetClause obj) {}
    public void visit(OrderByItem obj) {}
    public void visit(XMLElement obj) {}
    public void visit(XMLAttributes obj) {}
    public void visit(XMLForest obj) {}
    public void visit(XMLNamespaces obj) {}
    public void visit(TextTable obj) {}
    public void visit(TextLine obj) {}
    public void visit(XMLTable obj) {}
    public void visit(DerivedColumn obj) {}
    public void visit(XMLSerialize obj) {}
    public void visit(XMLQuery obj) {}
    public void visit(QueryString obj) {}
    public void visit(XMLParse obj) {}
    public void visit(ExpressionCriteria obj) {}
    public void visit(WithQueryCommand obj) {}
    public void visit(TriggerAction obj) {}
    public void visit(ArrayTable obj) {}

	public void visit(AlterView obj) {}
	public void visit(AlterProcedure obj) {}
    public void visit(AlterTrigger obj) {}

	public void visit(WindowFunction windowFunction) {}
	public void visit(WindowSpecification windowSpecification) {}
    
    // Visitor methods for language objects
    public void visit(IBatchedUpdateCommand obj) {
        visit((BatchedUpdateCommand)obj);
    }

    public void visit(IBetweenCriteria obj) {
        visit((BetweenCriteria)obj);
    }

    public void visit(ICaseExpression obj) {
        visit((CaseExpression)obj);
    }

    public void visit(ICompareCriteria obj) {
        visit((CompareCriteria)obj);
    }

    public void visit(ICompoundCriteria obj) {
        visit((CompoundCriteria)obj);
    }

    public void visit(IDelete obj) {
        visit((IProcedureContainer)obj);
    }

    public void visit(IExistsCriteria obj) {
        visit((ExistsCriteria)obj);
    }

    public void visit(IFrom obj) {
        visit((From)obj);
    }

    public void visit(IGroupBy obj) {
        visit((GroupBy)obj);
    }

    public void visit(IInsert obj) {
        visit((IProcedureContainer)obj);
    }

    public void visit(IIsNullCriteria obj) {
        visit((IsNullCriteria)obj);
    }

    public void visit(IJoinPredicate obj) {
        visit((JoinPredicate)obj);
    }

    public void visit(IJoinType obj) {
        visit((JoinType)obj);
    }

    public void visit(ILimit obj) {
        visit((Limit)obj);
    }

    public void visit(IMatchCriteria obj) {
        visit((MatchCriteria)obj);
    }

    public void visit(INotCriteria obj) {
        visit((NotCriteria)obj);
    }

    public void visit(IOption obj) {
        visit((Option)obj);
    }

    public void visit(IOrderBy obj) {
        visit((OrderBy)obj);
    }

    public void visit(IQuery obj) {
        visit((Query)obj);
    }

    public void visit(ISearchedCaseExpression obj) {
        visit((SearchedCaseExpression)obj);
    }

    public void visit(ISelect obj) {
        visit((Select)obj);
    }

    public void visit(ISetCriteria obj) {
        visit((SetCriteria)obj);
    }

    public void visit(ISetQuery obj) {
        visit((SetQuery)obj);
    }

    public void visit(IStoredProcedure obj) {
        visit((IProcedureContainer)obj);
    }

    public void visit(ISubqueryCompareCriteria obj) {
        visit((SubqueryCompareCriteria)obj);
    }

    public void visit(ISubqueryFromClause obj) {
        visit((SubqueryFromClause)obj);
    }

    public void visit(ISubquerySetCriteria obj) {
        visit((SubquerySetCriteria)obj);
    }

    public void visit(IUnaryFromClause obj) {
        visit((UnaryFromClause)obj);
    }

    public void visit(IUpdate obj) {
        visit((IProcedureContainer)obj);
    }

    public void visit(IInto obj) {
        visit((Into)obj);
    }

    public void visit(IDependentSetCriteria obj) {
        visit((DependentSetCriteria)obj);
    }

    public void visit(ICreate obj) {
        visit((Create)obj);
    }

    public void visit(IDrop obj) {
        visit((Drop)obj);
    }

    // Visitor methods for symbol objects
    public void visit(IAggregateSymbol obj) {
        visit((AggregateSymbol)obj);
    }

    public void visit(IAliasSymbol obj) {
        visit((AliasSymbol)obj);
    }

    public void visit(IMultipleElementSymbol obj) {
        visit((MultipleElementSymbol)obj);
    }

    public void visit(IConstant obj) {
        visit((Constant)obj);
    }

    public void visit(IElementSymbol obj) {
        visit((ElementSymbol)obj);
    }

    public void visit(IExpressionSymbol obj) {
        visit((ExpressionSymbol)obj);
    }

    public void visit(IFunction obj) {
        visit((Function)obj);
    }

    public void visit(IGroupSymbol obj) {
        visit((GroupSymbol)obj);
    }

    public void visit(IReference obj) {
        visit((Reference)obj);
    }

    public void visit(IScalarSubquery obj) {
        visit((ScalarSubquery)obj);
    }

    // Visitor methods for procedure language objects    
    public void visit(IAssignmentStatement obj) {
        visit((AssignmentStatement)obj);
    }

    public void visit(IBlock obj) {
        visit((Block)obj);
    }

    public void visit(ICommandStatement obj) {
        visit((CommandStatement)obj);
    }

    public void visit(ICreateProcedureCommand obj) {
        visit((CreateUpdateProcedureCommand)obj);
    }

    public void visit(ICriteriaSelector obj) {
        visit((CriteriaSelector)obj);
    }

    public void visit(IDeclareStatement obj) {
        visit((IAssignmentStatement)obj);
    }

    public void visit(IHasCriteria obj) {
        visit((HasCriteria)obj);
    }

    public void visit(IIfStatement obj) {
        visit((IfStatement)obj);
    }

    public void visit(IRaiseStatement obj) {
        visit((RaiseErrorStatement)obj);
    }

    public void visit(IBranchingStatement obj) {
        visit((BranchingStatement)obj);
    }

    public void visit(ITranslateCriteria obj) { throw new UnsupportedOperationException();
    }

    public void visit(IWhileStatement obj) {
        visit((WhileStatement)obj);
    }

    public void visit(ILoopStatement obj) {
        visit((LoopStatement)obj);
    }

    public void visit(IDynamicCommand obj) {
        visit((DynamicCommand)obj);
    }

    public void visit(IProcedureContainer obj) {
        visit((ProcedureContainer)obj);
    }

    public void visit(ISetClauseList obj) {
        visit((SetClauseList)obj);
    }

    public void visit(ISetClause obj) {
        visit((SetClause)obj);
    }

    public void visit(IOrderByItem obj) {
        visit((OrderByItem)obj);
    }

    public void visit(IXMLElement obj) {
        visit((XMLElement)obj);
    }

    public void visit(IXMLAttributes obj) {
        visit((XMLAttributes)obj);
    }

    public void visit(IXMLForest obj) {
        visit((XMLForest)obj);
    }

    public void visit(IXMLNamespaces obj) {
        visit((XMLNamespaces)obj);
    }

    public void visit(ITextTable obj) {
        visit((TextTable)obj);
    }

    public void visit(ITextLine obj) {
        visit((TextLine)obj);
    }

    public void visit(IXMLTable obj) {
        visit((XMLTable)obj);
    }

    public void visit(IDerivedColumn obj) {
        visit((DerivedColumn)obj);
    }

    public void visit(IXMLSerialize obj) {
        visit((XMLSerialize)obj);
    }

    public void visit(IXMLQuery obj) {
        visit((XMLQuery)obj);
    }

    public void visit(IQueryString obj) {
        visit((QueryString)obj);
    }

    public void visit(IXMLParse obj) {
        visit((XMLParse)obj);
    }

    public void visit(IExpressionCriteria obj) {
        visit((ExpressionCriteria)obj);
    }

    public void visit(IWithQueryCommand obj) {
        visit((WithQueryCommand)obj);
    }

    public void visit(ITriggerAction obj) {
        visit((TriggerAction)obj);
    }

    public void visit(IArrayTable obj) {
        visit((ArrayTable)obj);
    }

    public void visit(IAlterView obj) {
        visit((AlterView)obj);
    }

    public void visit(IAlterProcedure obj) {
        visit((AlterProcedure)obj);
    }

    public void visit(IAlterTrigger obj) {
        visit((AlterTrigger)obj);
    }

    public void visit(IWindowFunction obj) {
        visit((WindowFunction)obj);
    }

    public void visit(IWindowSpecification obj) {
        visit((WindowSpecification)obj);
    }

    public void visit(IArray obj) {
        throw new UnsupportedOperationException();
    }

    public void visit(IObjectTable obj) {
        throw new UnsupportedOperationException();
    }

    public void visit(IExceptionExpression obj) {
        throw new UnsupportedOperationException();
    }

    public void visit(IReturnStatement obj) {
        throw new UnsupportedOperationException();
    }
}
