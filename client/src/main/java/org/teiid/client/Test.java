/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.client;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminFactory;
import org.teiid.jdbc.TeiidDriver;

/**
 *
 */
public class Test {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) {

        try {

            String host = "goshawk"; //$NON-NLS-1$
            final String TEST_VDB = "<vdb name=\"ping\" version=\"1\">" + //$NON-NLS-1$
                                    "<model visible=\"true\" name=\"Foo\" type=\"PHYSICAL\" path=\"/dummy/Foo\">"
                                    + //$NON-NLS-1$
                                    "<source name=\"s\" translator-name=\"loopback\"/>"
                                    + //$NON-NLS-1$
                                    "<metadata type=\"DDL\"><![CDATA[CREATE FOREIGN TABLE G1 (e1 string, e2 integer);]]> </metadata>"
                                    + //$NON-NLS-1$
                                    "</model>" + //$NON-NLS-1$
                                    "</vdb>"; //$NON-NLS-1$ +

            Admin admin = AdminFactory.getInstance().createAdmin(host, 9999, "admin", "secret".toCharArray()); //$NON-NLS-1$ //$NON-NLS-2$

            // ping admin
            System.out.println("Pinging admin");
            admin.getSessions();

            // ping jdbc
            System.out.println("Pinging JDBC");
            Connection teiidJdbcConnection = null;
            String url = "jdbc:teiid:ping@mm://" + host + ":31000"; //$NON-NLS-1$ //$NON-NLS-2$

            System.out.println("Deploying ping-vdb");
            admin.deploy("ping-vdb.xml", new ByteArrayInputStream(TEST_VDB.getBytes())); //$NON-NLS-1$

            try {
                System.out.println("Connecting using TeiidDriver");
                String urlAndCredentials = url + ";user=user;password=user;"; //$NON-NLS-1$              
                teiidJdbcConnection = TeiidDriver.getInstance().connect(urlAndCredentials, null);
                System.out.println("Passed test");
                //pass
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                System.out.println("Undeploying ping-vdb");
                admin.undeploy("ping-vdb.xml"); //$NON-NLS-1$

                if (teiidJdbcConnection != null) {
                    System.out.println("Undeploying teiid jdbc connection");
                    teiidJdbcConnection.close();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

}
