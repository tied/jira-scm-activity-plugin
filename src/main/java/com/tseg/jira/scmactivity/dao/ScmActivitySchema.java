/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tseg.jira.scmactivity.config.ScmActivityConfigMgr;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmActivitySchema {
    
    private static final Logger LOGGER = Logger.getLogger(ScmActivitySchema.class);
    private static ScmActivitySchema scmActivitySchema = null;
    
    
    private final String MYSQL_SCM_ACTIVITY = "CREATE TABLE IF NOT EXISTS scm_activity ("
            + "ID BIGINT NOT NULL AUTO_INCREMENT,"
            + "issueKey VARCHAR(50) NOT NULL,"
            + "changeId VARCHAR(50) NOT NULL,"
            + "changeDate VARCHAR(50) NOT NULL,"
            + "changeAuthor VARCHAR(50) NOT NULL,"
            + "changeLink VARCHAR(255) NULL,"
            + "changeType VARCHAR(50) NOT NULL,"
            + "changeBranch VARCHAR(255) NULL,"
            + "changeTag VARCHAR(50) NULL,"
            + "changeStatus VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_issuekey_changeid_type UNIQUE (issueKey, changeId, changeType)"
            + ")";
    private final String MYSQL_SCM_MESSAGE = "CREATE TABLE IF NOT EXISTS scm_message ("
            + "ID BIGINT NOT NULL AUTO_INCREMENT,"
            + "scmActivityID BIGINT NOT NULL,"
            + "message TEXT NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_activity_id UNIQUE (scmActivityID),"
            + "CONSTRAINT FK_scm_message_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity(ID) ON DELETE CASCADE"
            + ")";
    private final String MYSQL_SCM_FILES = "CREATE TABLE IF NOT EXISTS scm_files ("
            + "ID BIGINT NOT NULL AUTO_INCREMENT,"
            + "scmActivityID BIGINT NOT NULL,"
            + "fileName VARCHAR(255) NOT NULL,"
            + "fileAction VARCHAR(50) NOT NULL,"
            + "fileVersion VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT FK_scm_files_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity (ID) ON DELETE CASCADE"
            + ")";
    private final String MYSQL_SCM_JOB = "CREATE TABLE IF NOT EXISTS scm_job ("
            + "ID BIGINT NOT NULL AUTO_INCREMENT,"
            + "scmActivityID BIGINT NOT NULL,"
            + "jobName VARCHAR(255) NOT NULL,"
            + "jobLink VARCHAR(255) NULL,"
            + "jobStatus VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_activity_id_jobname UNIQUE (jobName, scmActivityID),"
            + "CONSTRAINT FK_scm_job_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity (ID) ON DELETE CASCADE"
            + ")";
    
    
    private final String MSSQL_SCM_ACTIVITY = "IF object_id('scm_activity', 'U') is null CREATE TABLE scm_activity ("
            + "ID BIGINT NOT NULL IDENTITY(1,1),"
            + "issueKey VARCHAR(50) NOT NULL,"
            + "changeId VARCHAR(50) NOT NULL,"
            + "changeDate VARCHAR(50) NOT NULL,"
            + "changeAuthor VARCHAR(50) NOT NULL,"
            + "changeLink VARCHAR(255) NULL,"
            + "changeType VARCHAR(50) NOT NULL,"
            + "changeBranch VARCHAR(255) NULL,"
            + "changeTag VARCHAR(50) NULL,"
            + "changeStatus VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_issuekey_changeid_type UNIQUE (issueKey, changeId, changeType)"
            + ")";
    private final String MSSQL_SCM_MESSAGE = "IF object_id('scm_message', 'U') is null CREATE TABLE scm_message ("
            + "ID BIGINT NOT NULL IDENTITY(1,1),"
            + "scmActivityID BIGINT NOT NULL,"
            + "message TEXT NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_activity_id UNIQUE (scmActivityID),"
            + "CONSTRAINT FK_scm_message_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity(ID) ON DELETE CASCADE"
            + ")";
    private final String MSSQL_SCM_FILES = "IF object_id('scm_files', 'U') is null CREATE TABLE scm_files ("
            + "ID BIGINT NOT NULL IDENTITY(1,1),"
            + "scmActivityID BIGINT NOT NULL,"
            + "fileName VARCHAR(255) NOT NULL,"
            + "fileAction VARCHAR(50) NOT NULL,"
            + "fileVersion VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT FK_scm_files_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity (ID) ON DELETE CASCADE"
            + ")";
    private final String MSSQL_SCM_JOB = "IF object_id('scm_job', 'U') is null CREATE TABLE scm_job ("
            + "ID BIGINT NOT NULL IDENTITY(1,1),"
            + "scmActivityID BIGINT NOT NULL,"
            + "jobName VARCHAR(255) NOT NULL,"
            + "jobLink VARCHAR(255) NULL,"
            + "jobStatus VARCHAR(50) NULL,"
            + "PRIMARY KEY (ID),"
            + "CONSTRAINT scm_activity_id_jobname UNIQUE (jobName, scmActivityID),"
            + "CONSTRAINT FK_scm_job_activity FOREIGN KEY (scmActivityID) REFERENCES scm_activity (ID) ON DELETE CASCADE"
            + ")";
    
    private ScmActivitySchema() {}

    public static ScmActivitySchema getInstance() {
        if ( scmActivitySchema == null ) {
            scmActivitySchema = new ScmActivitySchema();
        }
        return scmActivitySchema;
    }
    
    /**
     * Method to initialize tables on database
     * @param connection
     * @return 
     */
    public ScmMessageBean initialize(Connection connection) {
        //Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ScmMessageBean messageBean = new ScmMessageBean();
        int result = 0;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();            
            statement = connection.createStatement();
            
            //get app properties
            ApplicationProperties props = ComponentAccessor.getApplicationProperties();        
            String db_url = props.getString(ScmActivityConfigMgr.DB_URL);
            
            if( db_url.contains("sqlserver") ) {
                LOGGER.info("matched");
                statement.addBatch(MSSQL_SCM_ACTIVITY);
                statement.addBatch(MSSQL_SCM_MESSAGE);
                statement.addBatch(MSSQL_SCM_FILES);
                statement.addBatch(MSSQL_SCM_JOB);
            } else if( db_url.contains("mysql") ) {
                statement.addBatch(MYSQL_SCM_ACTIVITY);
                statement.addBatch(MYSQL_SCM_MESSAGE);
                statement.addBatch(MYSQL_SCM_FILES);
                statement.addBatch(MYSQL_SCM_JOB); 
            }
            
            int[] execs = statement.executeBatch();
            
            LOGGER.debug("Batch execution count: "+ execs.length);
            if( execs.length == 4 ) {
                messageBean.setId(1);
                messageBean.setMessage("[Info] Connection Passed! Schema initialize is complete.");
            } else {
                messageBean.setId(0);
                messageBean.setMessage("Connection Passed! Schema initialize failed. "
                    + "Create these <a target='_blank' href='https://github.com/the-scm-enthusiast-group/jira-scm-activity-plugin/tree/master/sql'>schema tables</a> manually");
            }
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
            messageBean.setId(result);
            messageBean.setMessage("Connection Passed! Schema initialize failed. "
                    + "Create these <a target='_blank' href='https://github.com/the-scm-enthusiast-group/jira-scm-activity-plugin/tree/master/sql'>schema tables</a> manually");
            return messageBean;
        }
        finally{
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
                //if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        return messageBean;
    }    
}
