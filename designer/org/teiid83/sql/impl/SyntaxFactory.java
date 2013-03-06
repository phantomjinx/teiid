/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid83.sql.impl;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.IJoinType;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISPParameter.ParameterInfo;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;

/**
 *
 */
public class SyntaxFactory implements IQueryFactory<Expression, 
                                                                                            Expression, 
                                                                                            FromClause, 
                                                                                            ElementSymbol, 
                                                                                            Command, 
                                                                                            QueryCommand, 
                                                                                            Criteria, 
                                                                                            Constant, 
                                                                                            Block, 
                                                                                            Expression, 
                                                                                            GroupSymbol, 
                                                                                            JoinType> {

    @Override
    public IFunction createFunction(String name, List<? extends Expression> arguments) {
        if (arguments == null) {
            arguments = new ArrayList<Expression>();
        }
        
        return new Function(name, arguments.toArray(new Expression[0]));
    }

    @Override
    public IAggregateSymbol createAggregateSymbol(String functionName,
                                                  IAggregateSymbol.Type functionType,
                                                  boolean isDistinct,
                                                  Expression expression) {
        
        AggregateSymbol aggregateSymbol = new AggregateSymbol(functionName, isDistinct, expression);
        aggregateSymbol.setAggregateFunction(functionType);
        return aggregateSymbol;
    }

    @Override
    public IElementSymbol createElementSymbol(String name) {
        return new ElementSymbol(name);
    }

    @Override
    public IElementSymbol createElementSymbol(String name,
                                              boolean displayFullyQualified) {
        return new ElementSymbol(name, displayFullyQualified);
    }

    @Override
    public IAliasSymbol createAliasSymbol(String name,
                                          Expression symbol) {
        return new AliasSymbol(name, symbol);
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name) {
        return new GroupSymbol(name);
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name,
                                          String definition) {
        return new GroupSymbol(name, definition);
    }

    @Override
    public ExpressionSymbol createExpressionSymbol(String name,
                                                   Expression expression) {
        return new ExpressionSymbol(name, expression);
    }

    @Override
    public IMultipleElementSymbol createMultipleElementSymbol() {
        return new MultipleElementSymbol();
    }

    @Override
    public IConstant createConstant(Object value) {
        return new Constant(value);
    }

    @Override
    public IDeclareStatement createDeclareStatement(ElementSymbol variable,
                                                    String valueType) {
        return new DeclareStatement(variable, valueType);
    }

    @Override
    public ICommandStatement createCommandStatement(Command command) {
        return new CommandStatement(command);
    }

    @Override
    public IRaiseStatement createRaiseStatement(Expression expression) {
        return new RaiseStatement(expression);
    }

    @Override
    public IQuery createQuery() {
        return new Query();
    }

    @Override
    public ISetQuery createSetQuery(Operation operation,
                                    boolean all,
                                    QueryCommand leftQuery,
                                    QueryCommand rightQuery) {
        return new SetQuery(operation, all, leftQuery, rightQuery);
    }

    @Override
    public ISetQuery createSetQuery(Operation operation) {
        return new SetQuery(operation);
    }

    @Override
    public ICompareCriteria createCompareCriteria() {
        return new CompareCriteria();
    }

    @Override
    public ICompareCriteria createCompareCriteria(Expression expression1,
                                                  int operator,
                                                  Expression expression2) {
        return new CompareCriteria(expression1, operator, expression2);
    }

    @Override
    public IIsNullCriteria createIsNullCriteria() {
        return new IsNullCriteria();
    }

    @Override
    public IIsNullCriteria createIsNullCriteria(Expression expression) {
        return new IsNullCriteria(expression);
    }

    @Override
    public INotCriteria createNotCriteria() {
        return new NotCriteria();
    }

    @Override
    public INotCriteria createNotCriteria(Criteria criteria) {
        return new NotCriteria(criteria);
    }

    @Override
    public IMatchCriteria createMatchCriteria() {
        return new MatchCriteria();
    }

    @Override
    public ISetCriteria createSetCriteria() {
        return new SetCriteria();
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria() {
        return new SubquerySetCriteria();
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria(Expression expression,
                                                          QueryCommand command) {
        return new SubquerySetCriteria(expression, command);
    }

    @Override
    public ISubqueryCompareCriteria createSubqueryCompareCriteria(Expression leftExpression,
                                                                  QueryCommand command,
                                                                  int operator,
                                                                  int predicateQuantifier) {
        return new SubqueryCompareCriteria(leftExpression, command, operator, predicateQuantifier);
    }

    @Override
    public IScalarSubquery createScalarSubquery(QueryCommand queryCommand) {
        return new ScalarSubquery(queryCommand);
    }

    @Override
    public IBetweenCriteria createBetweenCriteria(ElementSymbol elementSymbol,
                                                  Constant constant1,
                                                  Constant constant2) {
        return new BetweenCriteria(elementSymbol, constant1, constant2);
    }

    @Override
    public ICompoundCriteria createCompoundCriteria(int operator, List<? extends Criteria> criteria) {
        return new CompoundCriteria(operator, criteria);
    }

    @Override
    public IExistsCriteria createExistsCriteria(QueryCommand queryCommand) {
        return new ExistsCriteria(queryCommand);
    }

    @Override
    public IBlock createBlock() {
        return new Block();
    }

    @Override
    public ICreateProcedureCommand createCreateProcedureCommand(Block block) {
        return new CreateProcedureCommand(block);
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol,
                                                          Expression expression) {
        return new AssignmentStatement(elementSymbol, expression);
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(ElementSymbol elementSymbol,
                                                          QueryCommand queryCommand) {
        return new AssignmentStatement(elementSymbol, queryCommand);
    }

    @Override
    public ISelect createSelect() {
        return new Select();
    }

    @Override
    public ISelect createSelect(List<? extends Expression> symbols) {
        return new Select(symbols);
    }

    @Override
    public IFrom createFrom() {
        return new From();
    }

    @Override
    public IFrom createFrom(List<? extends FromClause> fromClauses) {
        return new From(fromClauses);
    }

    @Override
    public IUnaryFromClause createUnaryFromClause(GroupSymbol symbol) {
        return new UnaryFromClause(symbol);
    }

    @Override
    public ISubqueryFromClause createSubqueryFromClause(String name,
                                                        QueryCommand command) {
        return new SubqueryFromClause(name, command);
    }

    @Override
    public IJoinType getJoinType(IJoinType.Types joinType) {
        switch (joinType) {
            case JOIN_INNER:
                return JoinType.JOIN_INNER;
            case JOIN_RIGHT_OUTER:
                return JoinType.JOIN_RIGHT_OUTER;
            case JOIN_LEFT_OUTER:
                return JoinType.JOIN_LEFT_OUTER;
            case JOIN_FULL_OUTER:
                return JoinType.JOIN_FULL_OUTER;
            case JOIN_CROSS:
                return JoinType.JOIN_CROSS;
            case JOIN_UNION:
                return JoinType.JOIN_UNION;
            case JOIN_SEMI:
                return JoinType.JOIN_SEMI;
            case JOIN_ANTI_SEMI:
                return JoinType.JOIN_ANTI_SEMI;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public IJoinPredicate createJoinPredicate(FromClause leftClause,
                                              FromClause rightClause,
                                              JoinType joinType) {
        return new JoinPredicate(leftClause, rightClause, joinType);
    }

    @Override
    public IJoinPredicate createJoinPredicate(FromClause leftClause,
                                              FromClause rightClause,
                                              JoinType joinType,
                                              List<Criteria> criteria) {
        return new JoinPredicate(leftClause, rightClause, joinType, criteria);
    }

    @Override
    public IGroupBy createGroupBy() {
        return new GroupBy();
    }

    @Override
    public IOrderBy createOrderBy() {
        return new OrderBy();
    }

    @Override
    public IOption createOption() {
        return new Option();
    }

    @Override
    public IUpdate createUpdate() {
        return new Update();
    }

    @Override
    public IDelete createDelete() {
        return new Delete();
    }

    @Override
    public IInsert createInsert() {
        return new Insert();
    }

    @Override
    public IStoredProcedure createStoredProcedure() {
        return new StoredProcedure();
    }

    @Override
    public ISPParameter createSPParameter(int index,
                                          Expression expression) {
        return new SPParameter(index, expression);
    }

    @Override
    public ISPParameter createSPParameter(int index,
                                          ParameterInfo parameterType,
                                          String name) {
        return new SPParameter(index, parameterType.index(), name);
    }

    @Override
    public IReference createReference(int index) {
        return new Reference(index);
    }

    @Override
    public IMetadataID createMetadataID(String id,
                                        Class clazz) {
        return new TempMetadataID(id, clazz);
    }

    @Override
    public IStoredProcedureInfo createStoredProcedureInfo() {
        return new StoredProcedureInfo();
    }

    @Override
    public IQueryNode createQueryNode(String queryPlan) {
        return new QueryNode(queryPlan);
    }
}
