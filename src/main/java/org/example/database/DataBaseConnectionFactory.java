package org.example.database;

import java.sql.Connection;

//scop : sa ne fie usor sa diferetiem intre diferitele bd : library / test_library
public class DataBaseConnectionFactory {
    private static final String SCHEMA = "library";
    private static final String TEST_SCHEMA = "test_library";

    public static JDBConnectionWrapper getConnectionWrapper(boolean test){
        if (test){
            return new JDBConnectionWrapper(TEST_SCHEMA) ;
        }
        else {
            return new JDBConnectionWrapper(SCHEMA);
        }
        //pt mai multe BD uri , se calibreaza
    }
}
