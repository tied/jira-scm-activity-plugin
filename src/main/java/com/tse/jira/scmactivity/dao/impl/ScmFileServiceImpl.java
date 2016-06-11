/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.scmactivity.dao.impl;

import com.tse.jira.scmactivity.dao.ScmActivityDB;
import java.sql.SQLException;
import com.tse.jira.scmactivity.dao.ScmFileService;
import com.tse.jira.scmactivity.model.ScmActivityBean;
import com.tse.jira.scmactivity.model.ScmChangeSetBean;
import com.tse.jira.scmactivity.model.ScmFileBean;
import com.tse.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmFileServiceImpl implements ScmFileService {

    private static final Logger LOGGER = Logger.getLogger(ScmFileServiceImpl.class);
    private static ScmFileServiceImpl scmFileService = null;
        
    private ScmFileServiceImpl() { }

    public static ScmFileServiceImpl getInstance() {
        if ( scmFileService == null ) {
            scmFileService = new ScmFileServiceImpl();
        }
        return scmFileService;
    }
    
    @Override
    public ScmMessageBean setScmFiles(List<ScmFileBean> changeFiles, long scmActivityID, Connection connection) {
        PreparedStatement statement = null;
        ScmMessageBean messageBean = new ScmMessageBean();
        int result = 0;
        
        try {
            
            if( scmActivityID > 0 ) {
                                
                deleteScmFiles(scmActivityID, connection); //clean if existing
                
                String QUERY = "INSERT INTO scm_files (fileName,fileAction,fileVersion,scmActivityID) "
                            + "values (?,?,?,?)";
                               
                for(ScmFileBean fileBean : changeFiles) { //add new
                    statement = connection.prepareStatement(QUERY);                    
                    statement.setString(1, fileBean.getFileName());
                    statement.setString(2, fileBean.getFileAction());
                    statement.setString(3, fileBean.getFileVersion());
                    statement.setLong(4, scmActivityID);
                    result = statement.executeUpdate();
                }
                
                messageBean.setId(result);
                messageBean.setMessage("[Info] activity files rows for scm id["+scmActivityID+"] is updated.");
            }
        } catch(SQLException ex) {
            LOGGER.error(ex);
            messageBean.setId(result);
            messageBean.setMessage(ex.getMessage());
            return messageBean;
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
    public ScmMessageBean setScmFiles(ScmChangeSetBean activityBean) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = ScmActivityDB.getInstance().getConnection();
        long scmActivityID = ScmActivityServiceImpl.getInstance()
                .getScmActivityID(activityBean.getIssueKey(), activityBean.getChangeId(),
                        activityBean.getChangeType(), connection);
        if( scmActivityID > 0 ) {
            messageBean = setScmFiles(activityBean.getChangeFiles(), scmActivityID, connection);
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
    public List<ScmFileBean> getScmFiles(long scmActivityID, Connection connection) {
        
        List<ScmFileBean> files = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String QUERY = "SELECT * FROM scm_files WHERE scmActivityID=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            resultSet = statement.executeQuery();
            
            if( resultSet != null ) {
                files = new ArrayList<ScmFileBean>();
                ScmFileBean fileBean = null;
                while( resultSet.next() ){
                    fileBean = new ScmFileBean();
                    fileBean.setId(resultSet.getLong("ID"));
                    fileBean.setFileName(resultSet.getString("fileName"));
                    fileBean.setFileAction(resultSet.getString("fileAction") );
                    fileBean.setFileVersion(resultSet.getString("fileVersion") );
                    files.add(fileBean);
                }
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
        return files;
    }

    @Override
    public void deleteScmFile(String issueKey, String changeId, String changeType, long fileId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(issueKey, changeId, changeType, connection);
            
            if( scmActivityID > 0 && fileId > 0 ) {
                String QUERY = "DELETE FROM scm_files WHERE scmActivityID=? AND ID=?";
                statement = connection.prepareStatement(QUERY);
                statement.setLong(1, scmActivityID);
                statement.setLong(2, fileId);
                statement.executeUpdate();
            }
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
        }
        finally{
            try {
                if( statement != null ) statement.close();
                if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
    }
    
    @Override
    public void deleteScmFiles(long scmActivityID, Connection connection) {
        PreparedStatement statement = null;        
        try {
            String QUERY = "DELETE FROM scm_files WHERE scmActivityID=?";
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
    
}
