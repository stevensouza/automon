package com.stevesouza.jdk;

import java.sql.*;

/**
 * Created by stevesouza on 3/30/15.
 */
public class JdbcInteraction {

    public JdbcInteraction() throws ClassNotFoundException {
        Class.forName("org.hsqldb.jdbcDriver");
    }


    /**
     * Class.forName("org.hsqldb.jdbcDriver");


     // Connect to the database and monitor the returned Connection.  That is all you have to do!!!
     // All SQL, Exceptions and method calls on JDBC interfaces will now be monitored!
     Connection conn = MonProxyFactory.monitor(DriverManager.getConnection("jdbc:hsqldb:.",  "sa", ""));
     Statement st=conn.createStatement();
     ResultSet rs=st.executeQuery("select * from INFORMATION_SCHEMA.SYSTEM_TYPEINFO where LOCAL_TYPE_NAME IN ('INTEGER', 'DECIMAL', 'TINYINT') order by 1 desc");


     // The formattedDataSet is another API of mine that renders TabularData among other things.
     String html="";
     html=fds.getFormattedDataSet(new ResultSetConverter(rs), "htmlTable");
     rs.close();
     st.close();

     // Monitor the PreparedStatement.  Note the SQL Detail report will show how many times the PreparedStatement
     // was reused.
     PreparedStatement ps=conn.prepareStatement("select * from INFORMATION_SCHEMA.SYSTEM_TYPEINFO where LOCAL_TYPE_NAME=?");
     ps.setString(1, "INTEGER");

     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();
     rs = ps.executeQuery();

     // create html for the last resultSet only
     String html1="";
     html1=fds.getFormattedDataSet(new ResultSetConverter(rs), "htmlTable");


     // Run and Monitor another couple queries
     st=conn.createStatement();
     rs=st.executeQuery("select TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE from INFORMATION_SCHEMA.SYSTEM_TABLES");
     String html2="";
     html2=fds.getFormattedDataSet(new ResultSetConverter(rs), "htmlTable");

     rs=st.executeQuery("select * from INFORMATION_SCHEMA.SYSTEM_USERS");
     String html3="";
     html3=fds.getFormattedDataSet(new ResultSetConverter(rs), "htmlTable");

     // Throw an exception and show that it is also monitored in jamonadmin.jsp and exceptions.jsp
     // Note also even though the catch block is empty it will show up in these pages.
     try {
     // get a query to throw an Exception.  If enabled will show in jamon report and sql details.
     st.executeQuery("select * from i_do_not_exist");

     } catch (Exception e) {}



     conn.close();
     */
    private Connection conn;

    public void runQueries() throws Exception {
        conn = DriverManager.getConnection("jdbc:hsqldb:.", "sa", "");
        queryDataTypes();
        testPreparedStatement();
        generateSqlException();

        conn.close();

    }

    private void queryDataTypes() throws SQLException {
        String dataTypes = executeQuery("select * from INFORMATION_SCHEMA.SYSTEM_TYPEINFO order by 1 desc");
        System.out.println("      ** Hsqldb datatypes: "+dataTypes);
    }

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
