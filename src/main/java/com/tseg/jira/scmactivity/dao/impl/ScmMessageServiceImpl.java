/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import com.tseg.jira.scmactivity.dao.ScmActivityDB;
import java.sql.SQLException;
import com.tseg.jira.scmactivity.dao.ScmMessageService;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmMessageServiceImpl implements ScmMessageService {

    private static final Logger LOGGER = Logger.getLogger(ScmMessageServiceImpl.class);
    private static ScmMessageServiceImpl scmMessageService = null;
        
    private ScmMessageServiceImpl() { }

    public static ScmMessageServiceImpl getInstance() {
        if ( scmMessageService == null ) {
            scmMessageService = new ScmMessageServiceImpl();
        }
        return scmMessageService;
    }
    
    @Override
    public ScmMessageBean setScmMessage(String changeMessage, long scmActivityID, Connection connection) {
        ScmMessageBean messageBean = new ScmMessageBean();
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            
            if( scmActivityID > 0 ) {                
                ScmMessageBean scmMessage = getScmMessage(scmActivityID, connection);
                
                if( scmMessage == null ) {
                    String QUERY = "INSERT INTO scm_message (scmActivityID, message) values (?, ?)";
                    statement = connection.prepareStatement(QUERY);
                    statement.setLong(1, scmActivityID);
                    statement.setString(2, changeMessage);
                    result = statement.executeUpdate();
                    
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] message for activity row ["+scmActivityID+"] is added.");
                    
                } else {
                    
                    String QUERY = "UPDATE scm_message SET message=? WHERE ID=?";
                    statement = connection.prepareStatement(QUERY);
                    statement.setString(1, changeMessage);
                    statement.setLong(2, scmMessage.getId());
                    result = statement.executeUpdate();
                    
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] activity message row ["+scmMessage.getId()+"] is updated.");
                }                
            } 
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            messageBean.setMessage(ex.getLocalizedMessage());            
        }
        finally{
            try {
                if( statement != null ) statement.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        return messageBean;
    }

    @Override
    public ScmMessageBean setScmMessage(ScmChangeSetBean activityBean) {
        ScmMessageBean messageBean = new ScmMessageBean();        
        Connection connection = ScmActivityDB.getInstance().getConnection();
        long scmActivityID = ScmActivityServiceImpl.getInstance()
                .getScmActivityID(activityBean.getIssueKey(), activityBean.getChangeId(),
                        activityBean.getChangeType(), connection);
        if( scmActivityID > 0 ) {
            messageBean = setScmMessage(activityBean.getChangeMessage(), scmActivityID, connection);
        } else {
            messageBean.setId(0);
            messageBean.setMessage("[Error] "+activityBean.getChangeType()+" Id ["+activityBean.getChangeId()+"] not "
                        + "exists on issue key ["+activityBean.getIssueKey()+"].");
        }
        try {
            if( connection != null ) connection.close();
        } catch (SQLException ex) {
            LOGGER.error(ex);
        }
        return messageBean;
    }
    
    @Override
    public void deleteScmMessage(long scmActivityID, Connection connection) {
        PreparedStatement statement = null;      
        try{
            String QUERY = "DELETE FROM scm_message WHERE scmActivityID=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            statement.executeUpdate();
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
        }
        finally{
            try {
                if( statement != null ) statement.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
    }
    
    @Override
    public ScmMessageBean getScmMessage(long scmActivityID, Connection connection) {
        ScmMessageBean messageBean = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try{
            String QUERY = "SELECT * FROM scm_message WHERE scmActivityID=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            resultSet = statement.executeQuery();
            if( resultSet.next() ){
                messageBean = new ScmMessageBean();
                messageBean.setId(resultSet.getLong("ID"));
                messageBean.setMessage(resultSet.getString("message"));
                LOGGER.debug("message id is found returning now - "+ messageBean.getId());
            }
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
        }
        finally{
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        
        return messageBean;
    }
    
}
