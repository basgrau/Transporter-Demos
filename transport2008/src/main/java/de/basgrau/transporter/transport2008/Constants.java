package de.basgrau.transporter.transport2008;

/**
 * Constants
 */
public class Constants {

    public static final String JNDI_PATH_UC2 =  "jdbc:h2:~/transporter-demo/uc2db";
    public static final String JNDI_PATH_UC3 =  "jdbc:h2:~/transporter-demo/uc3db_2008";
    public static final String TABELLE_UC2 = "USECASE2";
    public static final String TABELLE_UC3 = "USECASE3";

    public static final String UC2_SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + TABELLE_UC2
            + "(ID INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, SENDER VARCHAR(2), FILEDATA CLOB)";

}