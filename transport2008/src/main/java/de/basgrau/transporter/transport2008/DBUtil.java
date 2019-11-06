package de.basgrau.transporter.transport2008;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
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

    public static byte[] getBlob(String id) {
        byte[] data = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC2);
        if (connection == null)
            return data;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            
            ResultSet result = stmt.executeQuery("SELECT FILEDATA FROM " + Constants.TABELLE_UC2 + " WHERE ID="+id);
            while(result.next()){
                Blob blob = result.getBlob("FILEDATA");
                InputStream is = blob.getBinaryStream();
                data = getBytesFromInputStream(is);
            }
            return data;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return data;
        } finally {
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
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