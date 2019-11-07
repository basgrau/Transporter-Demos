package de.basgrau.transporter.transport2002;

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

    public static final int UC2 = 1;
    public static final int UC3 = 2;

    public static boolean createDBTable(int usecase) {
        String connectString = "";
        String sqlString = "";

        switch (usecase) {
        case UC2:
            connectString = Constants.JNDI_PATH_UC2;
            sqlString = Constants.UC2_SQL_CREATE;
            break;
        case UC3:
            connectString = Constants.JNDI_PATH_UC3;
            sqlString = Constants.UC3_SQL_CREATE;
            break;
        }

        Connection connection = createConnection(connectString);
        if (connection == null)
            return false;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int res = stmt.executeUpdate(sqlString);
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

    public static boolean writeClob(int usecase, Message message) {
        String connectString = "";
        String sqlString = "";

        switch (usecase) {
        case UC2:
            connectString = Constants.JNDI_PATH_UC2;
            sqlString = "INSERT INTO " + Constants.TABELLE_UC2 + " (SENDER, SENDDATE, FILEDATA) VALUES (?,?,?)";
            break;
        case UC3:
            connectString = Constants.JNDI_PATH_UC3;
            sqlString = "INSERT INTO " + Constants.TABELLE_UC3 + " (SENDER, SENDDATE, FILEDATA) VALUES (?,?,?)";
            break;
        }

        Connection connection = createConnection(connectString);
        if (connection == null)
            return false;

        byte[] data = message.getFiledata();
        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement(sqlString);
            System.out.println((pstmt == null) ? "null" : "nicht null");
            pstmt.setString(1, message.getSender());
            pstmt.setString(2, message.getSenddate());
            pstmt.setBlob(3, new ByteArrayInputStream(data));

            int res = pstmt.executeUpdate();
            System.out.println(res);
            if (res == 1) {
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

    public static int getId(int usecase, String senddate) {
        String connectString = "";
        String sqlString = "";

        switch (usecase) {
        case UC2:
            connectString = Constants.JNDI_PATH_UC2;
            sqlString = "SELECT ID FROM " + Constants.TABELLE_UC2 + " WHERE SENDDATE like '%" + senddate + "%'";
            break;
        case UC3:
            connectString = Constants.JNDI_PATH_UC3;
            sqlString = "SELECT ID FROM " + Constants.TABELLE_UC3 + " WHERE SENDDATE like '%" + senddate + "%'";
            break;
        }

        Connection connection = createConnection(connectString);
        if (connection == null)
            return -1;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int id = -1;
            ResultSet result = stmt.executeQuery(sqlString);
            while (result.next()) {
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

    public static byte[] getBlob(String id) {
        byte[] data = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return data;

        Statement stmt;
        try {
            stmt = connection.createStatement();

            ResultSet result = stmt.executeQuery("SELECT FILEDATA FROM " + Constants.TABELLE_UC3 + " WHERE ID =" + id);
            while (result.next()) {
                Blob blob = result.getBlob("FILEDATA");
                InputStream is = blob.getBinaryStream();
                data = getBytesFromInputStream(is);
            }
            System.out.println(data);
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

    public static String[] getIds() {
        String[] ids = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return ids;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            ArrayList<String> listIds = new ArrayList<String>();
            ResultSet result = stmt.executeQuery("SELECT id FROM " + Constants.TABELLE_UC3);
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

    public static int count() {
        byte[] data = null;
        Connection connection = createConnection(Constants.JNDI_PATH_UC3);
        if (connection == null)
            return -1;

        Statement stmt;
        try {
            stmt = connection.createStatement();
            int count = 0;
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) FROM " + Constants.TABELLE_UC3);
            while (result.next()) {
                count = result.getInt(1);
            }
            return count;
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

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
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
            System.err.println("Löschen für id " + id + " nicht erfolgreich!");
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