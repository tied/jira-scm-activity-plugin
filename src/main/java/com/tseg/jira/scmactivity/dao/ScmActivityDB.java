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
import java.sql.SQLException;
//import org.apache.commons.dbcp.BasicDataSource;
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
    public static int customEventId = 0;
    public static int expandCount = 1;
    public static int maxActive = 20;
    
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
        String max_active = props.getString(ScmActivityConfigMgr.MAX_ACTIVE);
        if( max_active != null && !"".equals(max_active) ){
            maxActive = Integer.parseInt(max_active);
        }
        String jira_event_id = props.getString(ScmActivityOptionMgr.JIRA_EVENT_ID);
        String expand_count = props.getString(ScmActivityOptionMgr.EXPAND_COUNT);
        
        if( !"".equals(db_driver) && !"".equals(db_url) ) {

            // load a properties        
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(db_driver);
            dataSource.setUrl(db_url);
            dataSource.setUsername(db_user);
            dataSource.setPassword(db_pass);
            
            //standard settings
            /**dataSource.setMaxActive(maxActive); 
            dataSource.setMaxWait(30000);
            dataSource.setMaxIdle(20);
            dataSource.setValidationQuery("select 1");
            dataSource.setValidationQueryTimeout(3);
            dataSource.setRemoveAbandoned(true);
            dataSource.setRemoveAbandonedTimeout(300);
            dataSource.setTestWhileIdle(true);*/
            
            dataSource.setInitialSize(10);
            dataSource.setMaxTotal(maxActive);
            dataSource.setMaxIdle(20);
            dataSource.setMaxWaitMillis(30000);
            dataSource.setValidationQuery("select 1");
            dataSource.setValidationQueryTimeout(3);
            dataSource.setRemoveAbandonedTimeout(300);
            dataSource.setRemoveAbandonedOnMaintenance(true);
            dataSource.setTestWhileIdle(true);
        }
        
        if( jira_event_id != null && !"".equals(jira_event_id) ){
            customEventId = Integer.parseInt(jira_event_id);
        }
            
        if( expand_count != null && !"".equals(expand_count) ){
            expandCount = Integer.parseInt(expand_count);
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
        Connection connection =null;
        long result = 0;
        
        connection = ScmActivityDB.getInstance().getConnection();
        if( connection == null ) {
            messageBean.setId(result);
            messageBean.setMessage("[Error] Unable to get database connection.");
            return messageBean;
        }
        messageBean = ScmActivitySchema.getInstance().initialize(connection);
        try {
            if( connection != null ) connection.close();
        } catch (SQLException ex) {
            LOGGER.error(ex);
        }        
        return messageBean;
    }
}
