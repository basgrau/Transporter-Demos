package de.basgrau.transporter.transport2008;

/**
 * Constants
 */
public class Constants {

    public static final String BASE_WEB_TARGET_2005 = "http://localhost:8005/api";
    public static final String BASE_UC3_PATH = "uc3ctrlr";

    public static final String JNDI_PATH_UC2 = "jdbc:h2:~/transporter-demo/uc2db";
    public static final String JNDI_PATH_UC3 = "jdbc:h2:~/transporter-demo/uc3db_2008";
    public static final String TABELLE_UC2 = "USECASE2";
    public static final String TABELLE_UC3 = "USECASE3";

    public static final String UC3_SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABELLE_UC3
            + " (id int not null PRIMARY KEY, SENDER VARCHAR(2), SENDDATE VARCHAR(25), STATUS VARCHAR(1), FILEDATA BLOB)";
    public static final String STATUS_ANNAHME = "A";
    public static final String STATUS_EINGANG = "E";
    public static final String STATUS_BEARBEITET = "B";
}