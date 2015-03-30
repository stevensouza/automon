package com.stevesouza.jdk;

import java.sql.*;

/**
 * Class that does simple jdbc interactions with an embedded hsqldb database.  It is used to demontrate jdbc monitoring.
 */
public class JdbcInteraction {

    private Connection conn;

    public JdbcInteraction() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }


    public void runQueries() throws Exception {
        conn = DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
        queryDataTypes();
        testPreparedStatement();
        generateSqlException();

        conn.close();

    }

    // just does a simple query with a statement on a known table
    private void queryDataTypes() throws SQLException {
        String dataTypes = executeQuery("select * from INFORMATION_SCHEMA.SYSTEM_TYPEINFO order by 1 desc");
        System.out.println("      ** Hsqldb datatypes: "+dataTypes);
    }

    // Execute the passed in query.  Note resources aren't properly closed which is ok for this demo.  Monitoring would show that
    // resources are not be closed when an exception is thrown.
    private String executeQuery(String query) throws SQLException {
        Statement st=conn.createStatement();
        ResultSet rs=st.executeQuery(query);

        StringBuilder results = new StringBuilder();
        while (rs.next()) {
            results.append(rs.getObject(1)).append(",");
        }
        rs.close();
        st.close();

        return results.toString();

    }

    // Purposefully throw an exception.  Automon monitoring will detect this.
    private void generateSqlException()  {
        try {
            // get a query to throw an Exception.
            executeQuery("select * from i_do_not_exist");
        } catch (Exception e) {
            System.out.println("      ** sql exception thrown: "+e);
        }
    }

    private void testPreparedStatement() throws SQLException {
        PreparedStatement ps=conn.prepareStatement("select * from INFORMATION_SCHEMA.SYSTEM_TYPEINFO where LOCAL_TYPE_NAME=?");
        ps.setString(1, "INTEGER");

        ResultSet rs = null;
        int LOOP_SIZE = 2;
        for (int i=0;i<LOOP_SIZE;i++) {
            rs = ps.executeQuery();
        }

        System.out.println("      ** PreparedStatement query was run "+LOOP_SIZE+" times, and has "+rs.getMetaData().getColumnCount() + " columns");
    }

    public static void main(String[] args) throws Exception {
        JdbcInteraction jdbc = new JdbcInteraction();
        jdbc.runQueries();
    }
}
