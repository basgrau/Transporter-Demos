package de.basgrau.transporter.transport2002;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.basgrau.transporter.shared.model.Message;

/**
 * DBUtil
 */
public class DBUtil {

    public static boolean createDBTable() {
        Connection connection = createConnection(Constants.JNDI_PATH_UC2);
        if (connection == null)
            return false;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int res = stmt.executeUpdate(Constants.UC2_SQL_CREATE);
            System.out.println(res);
            if (res == 0) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static boolean writeClob(Message message) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC2);
        if (connection == null)
            return false;

        byte[] data = message.getFiledata();
        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + Constants.TABELLE_UC2 + " (SENDER, SENDDATE, FILEDATA) VALUES (?,?,?)");
            System.out.println((pstmt == null) ? "null" : "nicht null");
            pstmt.setString(1, message.getSender());
            pstmt.setString(2, message.getSenddate());
            pstmt.setBlob(3, new ByteArrayInputStream(data));

            int res = pstmt.executeUpdate();
            System.out.println(res);
            if (res == 1){
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static int getId(String senddate) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC2);
        if (connection == null)
            return -1;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int id = -1;
            ResultSet result = stmt.executeQuery("SELECT ID FROM " + Constants.TABELLE_UC2 + " WHERE SENDDATE like '%"+senddate + "%'");
            while(result.next()){
                id = result.getInt("ID");
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }


    private static Connection createConnection(String conectionJNDI) {
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(conectionJNDI, "", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static Clob createClob(byte[] data) {
        Clob clob = null;
        java.io.Writer writer;

        return clob;
    }
}