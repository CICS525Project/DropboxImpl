package extra;

import java.sql.*;
import com.microsoft.sqlserver.jdbc.*;

public class SQLTest {

	public static void main(String[] args) 
    {

        // Connection string for your SQL Database server.
        // Change the values assigned to your_server, 
        // your_user@your_server,
        // and your_password.
        String connectionString = 
            "jdbc:sqlserver://your_server.database.windows.net:1433" + ";" +  
                "database=gettingstarted" + ";" + 
                "user=your_user@your_server" + ";" +  
                "password=your_password";

        // The types for the following variables are
        // defined in the java.sql library.
        Connection connection = null;  // For making the connection
        Statement statement = null;    // For the SQL statement
        ResultSet resultSet = null;    // For the result set, if applicable

        try
        {
            // Ensure the SQL Server driver class is available.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Establish the connection.
            connection = DriverManager.getConnection(connectionString);

            // Define the SQL string.
            String sqlString = 
                "CREATE TABLE Person (" + 
                    "[PersonID] [int] IDENTITY(1,1) NOT NULL," +
                    "[LastName] [nvarchar](50) NOT NULL," + 
                    "[FirstName] [nvarchar](50) NOT NULL)";

            // Use the connection to create the SQL statement.
            statement = connection.createStatement();

            // Execute the statement.
            statement.executeUpdate(sqlString);

            // Provide a message when processing is complete.
            System.out.println("Processing complete.");

        }
        // Exception handling
        catch (ClassNotFoundException cnfe)  
        {

            System.out.println("ClassNotFoundException " +
                               cnfe.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // Close resources.
                if (null != connection) connection.close();
                if (null != statement) statement.close();
                if (null != resultSet) resultSet.close();
            }
            catch (SQLException sqlException)
            {
                // No additional action if close() statements fail.
            }
        }

    }

}
