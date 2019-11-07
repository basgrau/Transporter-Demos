package de.basgrau.transporter.transport2005;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.basgrau.transporter.shared.model.Message;

/**
 * DBUtil
 */
public class DBUtil {
    public static final int GRUND_ALLE_IDS = 0;
    public static final int GRUND_OHNE_FILEDATA = 1;
    public static final int GRUND_MIT_FILEDATA = 2;
    public static final int GRUND_BEREITS_ABGERUFEN = 3;
    public static final int GRUND_NICHT_ABGERUFEN = 4;

    public static boolean createDBTable() {
        String connectString = Constants.JNDI_PATH_UC3;
        String sqlString = Constants.UC3_SQL_CREATE;

        Connection connection = createConnection(connectString);
        if (connection == null)
            return false;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int res = stmt.executeUpdate(sqlString);
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

    public static boolean insert(Message message) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return false;

        int id = -1;
        try {
            id = Integer.parseInt(message.getFileid().trim());
            System.out.println("FileID: " + id);
        } catch (NumberFormatException e) {
            System.err.println("FileID: '" + message.getFileid()+"'");
            return false;
        }

        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement(
                    "INSERT INTO " + Constants.TABELLE_UC3 + " (id, SENDER, SENDDATE, ABGERUFEN) VALUES (?,?,?,?)");
            System.out.println((pstmt == null) ? "null" : "nicht null");
            pstmt.setInt(1, id);
            pstmt.setString(2, message.getSender());
            pstmt.setString(3, message.getSenddate());
            pstmt.setString(4, "n");

            int res = pstmt.executeUpdate();
            System.out.println("PSTMT:" + res);
            if (res == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
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

    public static boolean delete(String id) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return false;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            System.out.println((stmt == null) ? "null" : "nicht null");
            boolean success = stmt.execute("DELETE FROM " + Constants.TABELLE_UC3 + " WHERE id = " + id);

            return success;
        } catch (Exception e) {
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

    public static boolean writeBlobUC3(String id, byte[] blob) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return false;

        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement("UPDATE " + Constants.TABELLE_UC3
                    + " SET FILEDATA = ? WHERE id = " + id);
            System.out.println((pstmt == null) ? "null" : "nicht null");
            pstmt.setBlob(1, new ByteArrayInputStream(blob));

            int res = pstmt.executeUpdate();
            if (res == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
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

    public static boolean updateAbgerufen(String id) {
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return false;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int res = stmt.executeUpdate("UPDATE " + Constants.TABELLE_UC3
            + " SET ABGERUFEN = 'y' WHERE id = " + id);
            if (res == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
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

    public static String[] getIds(int grund) {
        String[] ids = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return ids;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            ArrayList<String> listIds = new ArrayList<String>();
            String sqlStr = "SELECT id FROM " + Constants.TABELLE_UC3;

            switch(grund){
                case GRUND_OHNE_FILEDATA:
                    sqlStr = "SELECT id FROM " + Constants.TABELLE_UC3 + " WHERE FILEDATA IS NULL";
                break;
                case GRUND_MIT_FILEDATA:
                    sqlStr = "SELECT id FROM " + Constants.TABELLE_UC3 + " WHERE FILEDATA IS NOT NULL";
                break;
                case GRUND_BEREITS_ABGERUFEN:
                    sqlStr = "SELECT id FROM " + Constants.TABELLE_UC3 + " WHERE ABGERUFEN like 'y'";
                break;
                case GRUND_NICHT_ABGERUFEN:
                    sqlStr = "SELECT id FROM " + Constants.TABELLE_UC3 + " WHERE ABGERUFEN like 'n' AND FILEDATA IS NOT NULL";
                break;
            }
            
            ResultSet result = stmt.executeQuery(sqlStr);
            while (result.next()) {
                listIds.add("" + result.getInt("id"));
            }

            String[] idsArr = new String[listIds.size()];
            for (int i = 0; i < listIds.size(); i++) {
                    idsArr[i] = listIds.get(i);
            }

            return idsArr;
        } catch (SQLException e) {
            e.printStackTrace();
            return ids;
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
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return data;

        Statement stmt;
        try {
            stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery("SELECT FILEDATA FROM " + Constants.TABELLE_UC3 + " WHERE ID=" + id);
            while (result.next()) {
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

    public static Message getMessage(String id) {
        Message message = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return message;

        Statement stmt;
        try {
            stmt = connection.createStatement();

            ResultSet result = stmt
                    .executeQuery("SELECT SENDER, SENDDATE FROM " + Constants.TABELLE_UC3 + " WHERE ID =" + id);
            while (result.next()) {
                message = new Message();
                message.setSender(result.getString("SENDER"));
                message.setSenddate(result.getString("SENDDATE"));
                message.setFileid(id);
                message.setFiledata(null);
            }

            return message;
        } catch (SQLException e) {
            e.printStackTrace();
            return message;
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
}