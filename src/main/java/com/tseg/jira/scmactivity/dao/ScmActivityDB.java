/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tseg.jira.scmactivity.config.ScmActivityConfigMgr;
import com.tseg.jira.scmactivity.config.ScmActivityOptionMgr;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmActivityDB {
    private static final Logger LOGGER = Logger.getLogger(ScmActivityDB.class);

    private static ScmActivityDB scmActivityDB = null;
    private static BasicDataSource dataSource = null;
    public static long customEventId = 0;
    public static long expandCount = 1;
    
    private ScmActivityDB() {
        getDataSource(); //create datasource
    }

    public static ScmActivityDB getInstance() {
        if ( scmActivityDB == null ) {
            scmActivityDB = new ScmActivityDB();
        }
        return scmActivityDB;
    }

    private static BasicDataSource getDataSource()
    {
        LOGGER.debug("[Debug] DataSource Object is NULL so creating one.");

        //get app properties
        ApplicationProperties props = ComponentAccessor.getApplicationProperties();

        String db_user = props.getString(ScmActivityConfigMgr.DB_USER);
        String db_pass = props.getString(ScmActivityConfigMgr.DB_PASS);
        String db_driver = props.getString(ScmActivityConfigMgr.DB_DRIVER);
        String db_url = props.getString(ScmActivityConfigMgr.DB_URL);
        
        String jira_event_id = props.getString(ScmActivityOptionMgr.JIRA_EVENT_ID);
        String expand_count = props.getString(ScmActivityOptionMgr.EXPAND_COUNT);
        
        if( !"".equals(db_user) && !"".equals(db_pass) && !"".equals(db_driver)
                && !"".equals(db_url) ) {

            // load a properties        
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(db_driver);
            dataSource.setUrl(db_url);
            dataSource.setUsername(db_user);
            dataSource.setPassword(db_pass);

            //standard settings            
            dataSource.setInitialSize(20);
            dataSource.setMaxTotal(20);
            dataSource.setMaxWaitMillis(30000);            
            dataSource.setMaxIdle(20);
            dataSource.setValidationQuery("select 1");
            dataSource.setValidationQueryTimeout(3);            
            dataSource.setMinEvictableIdleTimeMillis(60000);
            dataSource.setTimeBetweenEvictionRunsMillis(300000);
            dataSource.setRemoveAbandonedTimeout(300);
            dataSource.setRemoveAbandonedOnMaintenance(true);
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
        }
        
        if( jira_event_id != null && !"".equals(jira_event_id) ){
            customEventId = Long.parseLong(jira_event_id);
        }
            
        if( expand_count != null && !"".equals(expand_count) ){
            expandCount = Long.parseLong(expand_count);
        }

        return dataSource;
    }

    public void inValidateDataSource() {
        dataSource = null;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            if ( dataSource == null ) getDataSource();
            if ( dataSource != null ) connection = dataSource.getConnection();
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
        }
        return connection;
    }
        
    public ScmMessageBean getConnectionTest() {
        ScmMessageBean messageBean = new ScmMessageBean();
        long result = 0;
        Connection connection =null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            
            //get connection
            connection = ScmActivityDB.getInstance().getConnection();
            if( connection == null ) {
                messageBean.setId(result);
                messageBean.setMessage("[Error] Unable to get database connection.");
                return messageBean;
            }
            
            String QUERY = "SELECT USER";         
            statement = connection.prepareStatement(QUERY);
            
            resultSet = statement.executeQuery();            
            if( resultSet.next() ){
                messageBean = ScmActivitySchema.getInstance().initialize(connection);
            }else{
                messageBean.setId(0);
                messageBean.setMessage("Connection Failed!");
            }            
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
            messageBean.setId(result);
            messageBean.setMessage(ex.getMessage());
            return messageBean;
        }
        finally{
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
                if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }        
        return messageBean;
    }
}
