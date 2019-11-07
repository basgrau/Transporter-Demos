package de.basgrau.transporter.transport2002;

/**
 * Constants
 */
public class Constants {

    public static final String BASE_WEB_TARGET = "http://localhost:8005/api";
    public static final String BASE_UC1_PATH = "uc1ctrlr";
    public static final String BASE_UC2_PATH = "uc2ctrlr";
    public static final String BASE_UC3_PATH = "uc3ctrlr";

    public static final String JNDI_PATH_UC2 = "jdbc:h2:~/transporter-demo/uc2db";
    public static final String JNDI_PATH_UC3 = "jdbc:h2:~/transporter-demo/uc3db_2002";
    public static final String TABELLE_UC2 = "USECASE2";
    public static final String TABELLE_UC3 = "USECASE3";

    public static final String UC2_SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABELLE_UC2
            + "(id int auto_increment not null, SENDER VARCHAR(2), SENDDATE VARCHAR(25), FILEDATA BLOB)";
    public static final String UC3_SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABELLE_UC3
            + "(id int auto_increment not null, SENDER VARCHAR(2), SENDDATE VARCHAR(25), FILEDATA BLOB)";

}