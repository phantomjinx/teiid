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
package org.teiid.dqp.internal.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.DeprecatedOngoingStubbing;
import org.teiid.client.util.ResultsFuture;
import org.teiid.core.util.UnitTestUtil;
import org.teiid.dqp.internal.datamgr.ConnectorManager;
import org.teiid.dqp.internal.datamgr.ConnectorManagerRepository;
import org.teiid.jdbc.FakeServer;
import org.teiid.jdbc.RequestOptions;
import org.teiid.jdbc.StatementCallback;
import org.teiid.jdbc.TeiidStatement;
import org.teiid.language.Command;
import org.teiid.language.QueryExpression;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.runtime.EmbeddedConfiguration;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.ReusableExecution;
import org.teiid.translator.TranslatorException;

@SuppressWarnings("nls")
public class TestExecutionReuse {

	private static final int EXEC_COUNT = 3;
	private static FakeServer server;
	
	private static class FakeReusableExecution implements ResultSetExecution, ReusableExecution<Object> {

		@Override
		public List<?> next() throws TranslatorException,
				DataNotAvailableException {
			return null;
		}

		@Override
		public void cancel() throws TranslatorException {
		}

		@Override
		public void close() {
		}

		@Override
		public void execute() throws TranslatorException {
		}

		@Override
		public void dispose() {
		}

		@Override
		public void reset(Command c, ExecutionContext executionContext,
				Object connection) {
		}
		
	}
	
	private static FakeReusableExecution execution;
	private static boolean isDisposed;
	
	@Before public void setup() throws DataNotAvailableException, TranslatorException {
		execution = Mockito.mock(FakeReusableExecution.class);
		ec = null;
		DeprecatedOngoingStubbing stubbing = Mockito.stub(execution.next()).toReturn((List) Arrays.asList((Object)null)).toReturn(null);
		for (int i = 1; i < EXEC_COUNT; i++) {
			stubbing.toReturn(Arrays.asList((Object)null)).toReturn(null);
		}
		Mockito.doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				synchronized (TestExecutionReuse.class) {
					isDisposed = true;
					TestExecutionReuse.class.notify();
				}
				return null;
			}
		}).when(execution).dispose();
	}

	private static ExecutionContext ec;

    @BeforeClass public static void oneTimeSetUp() throws Exception {
    	EmbeddedConfiguration config = new EmbeddedConfiguration();
    	config.setUserRequestSourceConcurrency(1);
    	server = new FakeServer(false);
		server.setConnectorManagerRepository(new ConnectorManagerRepository() {
			private ConnectorManager cm = new ConnectorManager("x", "y") {
				private ExecutionFactory<Object, Object> ef = new ExecutionFactory<Object, Object>() {
					
					@Override
					public ResultSetExecution createResultSetExecution(
							QueryExpression command,
							ExecutionContext executionContext,
							RuntimeMetadata metadata, Object connection)
							throws TranslatorException {
						ec = executionContext;
						return execution;
					};
					
					public boolean isSourceRequired() {
						return false;
					};
				};
				@Override
				public ExecutionFactory<Object, Object> getExecutionFactory() {
					return ef;
				}
				
				@Override
				public Object getConnectionFactory()
						throws TranslatorException {
					return null;
				}
				
			};
			@Override
			public ConnectorManager getConnectorManager(String connectorName) {
				return cm;
			}
		});
		server.start(config, false);
		server.deployVDB("PartsSupplier", UnitTestUtil.getTestDataPath() + "/PartsSupplier.vdb");
    }
    
    @AfterClass public static void oneTimeTearDown() throws Exception {
    	server.stop();
    }
    
	@Test public void testReusableAsynchContinuous() throws Exception {
		Connection c = server.createConnection("jdbc:teiid:partssupplier");
		Statement s = c.createStatement();
		TeiidStatement ts = s.unwrap(TeiidStatement.class);
		final ResultsFuture<Integer> result = new ResultsFuture<Integer>(); 
		ts.submitExecute("select part_id from parts order by part_id", new StatementCallback() {
			int rowCount;
			@Override
			public void onRow(Statement stmt, ResultSet rs) throws SQLException {
				rowCount++;
				if (rowCount == EXEC_COUNT) {
					stmt.close();
				}
			}
			
			@Override
			public void onException(Statement stmt, Exception e) {
				result.getResultsReceiver().exceptionOccurred(e);
			}
			
			@Override
			public void onComplete(Statement stmt) {
				result.getResultsReceiver().receiveResults(rowCount);
			}
		}, new RequestOptions().continuous(true));
		synchronized (TestExecutionReuse.class) {
			while (!isDisposed) {
				TestExecutionReuse.class.wait();
			}
		}
		assertEquals(EXEC_COUNT, result.get().intValue());
		assertTrue(ec.getCommandContext().isContinuous());
		Mockito.verify(execution, Mockito.times(1)).dispose();
		Mockito.verify(execution, Mockito.times(EXEC_COUNT)).execute();
		Mockito.verify(execution, Mockito.times(EXEC_COUNT)).close();
		Mockito.verify(execution, Mockito.times(EXEC_COUNT - 1)).reset((Command)Mockito.anyObject(), (ExecutionContext)Mockito.anyObject(), Mockito.anyObject());
	}

}
