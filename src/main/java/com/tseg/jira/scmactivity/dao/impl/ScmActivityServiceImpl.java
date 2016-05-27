/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import com.tseg.jira.scmactivity.dao.ScmActivityDB;
import java.sql.SQLException;
import com.tseg.jira.scmactivity.dao.ScmActivityService;
import com.tseg.jira.scmactivity.model.ScmActivityBean;
import com.tseg.jira.scmactivity.model.ScmActivityCustomFieldBean;
import com.tseg.jira.scmactivity.model.ScmActivityNotifyBean;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmFileBean;
import com.tseg.jira.scmactivity.model.ScmJobBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import com.tseg.jira.scmactivity.plugin.ScmActivityUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmActivityServiceImpl implements ScmActivityService {

    private static final Logger LOGGER = Logger.getLogger(ScmActivityServiceImpl.class);
    private static ScmActivityServiceImpl scmActivityService = null;
        
    private ScmActivityServiceImpl() { }

    public static ScmActivityServiceImpl getInstance() {
        if ( scmActivityService == null ) {
            scmActivityService = new ScmActivityServiceImpl();
        }
        return scmActivityService;
    }
    
    /**
     * Method to set new SCM Activity
     * @param activityBean
     * @return 
     */
    
    @Override
    public ScmMessageBean setScmActivity(ScmChangeSetBean activityBean) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(activityBean.getIssueKey(), activityBean.getChangeId(), 
                            activityBean.getChangeType(), connection);
            
            if( scmActivityID == 0 ) {
                
                String QUERY = "INSERT INTO scm_activity (issueKey, changeId, changeDate, changeAuthor, changeBranch,"
                        + "changeTag, changeStatus, changeLink, changeType) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                statement = connection.prepareStatement( QUERY, Statement.RETURN_GENERATED_KEYS );
                statement.setString(1, activityBean.getIssueKey().trim().toUpperCase());
                statement.setString(2, activityBean.getChangeId().trim());
                statement.setString(3, activityBean.getChangeDate().trim());
                statement.setString(4, activityBean.getChangeAuthor().trim());
                statement.setString(5, activityBean.getChangeBranch());
                statement.setString(6, activityBean.getChangeTag());
                statement.setString(7, activityBean.getChangeStatus());
                statement.setString(8, activityBean.getChangeLink());
                statement.setString(9, activityBean.getChangeType().trim());
                
                result = statement.executeUpdate();
                
                resultSet = statement.getGeneratedKeys(); //get last generated id
                
                if( resultSet.next() ) {
                    
                    long generated_scmActivityID = resultSet.getLong(1);
                    
                    if( activityBean.getChangeMessage() != null && !"".equals(activityBean.getChangeMessage()) ) {                    
                        ScmMessageServiceImpl.getInstance().setScmMessage(activityBean.getChangeMessage(), 
                                generated_scmActivityID, connection);
                    }
                    
                    if( activityBean.getChangeFiles() != null && !activityBean.getChangeFiles().isEmpty() ) {
                        ScmFileServiceImpl.getInstance().setScmFiles(activityBean.getChangeFiles(), 
                                generated_scmActivityID, connection);
                    }
                    
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] "+activityBean.getIssueKey() +" > "+activityBean.getChangeId()+""
                        + " activity row ["+generated_scmActivityID+"] is added.");
                }
                    
            } else {
                
                if( activityBean.getChangeUpdate() == true ) {
                    
                    String QUERY = "UPDATE scm_activity SET changeDate=?, changeAuthor=?, changeBranch=?,"
                        + "changeTag=?, changeStatus=?, changeLink=?, changeType=? WHERE ID=?";
                    
                    statement = connection.prepareStatement( QUERY );
                    statement.setString(1, activityBean.getChangeDate().trim());
                    statement.setString(2, activityBean.getChangeAuthor().trim());
                    statement.setString(3, activityBean.getChangeBranch());
                    statement.setString(4, activityBean.getChangeTag());
                    statement.setString(5, activityBean.getChangeStatus());
                    statement.setString(6, activityBean.getChangeLink());
                    statement.setString(7, activityBean.getChangeType().trim());
                    statement.setLong(8, scmActivityID);
                
                    result = statement.executeUpdate();
                
                    if( activityBean.getChangeMessage() != null && !"".equals(activityBean.getChangeMessage()) ) {                    
                        ScmMessageServiceImpl.getInstance().setScmMessage(activityBean.getChangeMessage(), 
                                scmActivityID, connection);
                    }
                    
                    if( activityBean.getChangeFiles() != null && !activityBean.getChangeFiles().isEmpty() ) {
                        ScmFileServiceImpl.getInstance().setScmFiles(activityBean.getChangeFiles(), 
                                scmActivityID, connection);
                    }
                        
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] "+activityBean.getIssueKey() +" > "+activityBean.getChangeId()+""
                            + " activity row ["+scmActivityID+"] is updated.");                    
                    
                } else {
                                        
                    messageBean.setId(1);
                    messageBean.setMessage("[Error] Skipping. There is "+activityBean.getChangeType() +" Id ["+activityBean.getChangeId()+"]"
                        + " already exists on issue key ["+activityBean.getIssueKey()+"].");                    
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
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
    
    
    /**
     * Method to delete SCM Activity
     * @param issueKey
     * @param changeId
     * @param changeType 
     * @return  
     */
    
    @Override
    public ScmMessageBean deleteScmActivity(String issueKey, String changeId, String changeType) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(issueKey, changeId, changeType, connection);
            
            if( scmActivityID > 0 ) {                                
                
                String QUERY = "DELETE FROM scm_activity WHERE ID=?";
                statement = connection.prepareStatement(QUERY);
                statement.setLong(1, scmActivityID);
                result=statement.executeUpdate();
                
                if( result == 1 ) { //now go ahead further                    
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] "+ changeType +" > "
                            + changeId +" > "+issueKey+" activity is deleted.");
                    
                    //delete associated files
                    ScmFileServiceImpl.getInstance()
                        .deleteScmFiles(scmActivityID, connection);
                    
                    //delete associated message
                    ScmMessageServiceImpl.getInstance()
                        .deleteScmMessage(scmActivityID, connection);
                
                    //delete associated jobs
                    ScmJobServiceImpl.getInstance()
                        .deleteScmJobs(scmActivityID, connection);
                                    
                }
                
            } else {
                messageBean.setId(result);
                messageBean.setMessage("[Error] Scm "+changeType+" Id ["+changeId+"] "
                        + "not exists on issue key ["+issueKey+"].");
            }
        }
        catch (SQLException ex) {
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
    

    /**
     * Method to get SCM Activity by Issue Key, Change Id, and Change Type
     * @param issueKey
     * @param changeId
     * @param changeType
     * @return 
     */
    
    @Override
    public ScmActivityBean getScmActivity(String issueKey, String changeId, String changeType) {
        ScmActivityBean scmBean = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
                
        try {
            
            connection = ScmActivityDB.getInstance().getConnection();

            String QUERY = "SELECT t1.ID AS ID, t1.issueKey AS issueKey, t1.changeId AS changeId, t1.changeType AS changeType, t1.changeDate AS changeDate, t1.changeAuthor AS changeAuthor, "
                    + "t1.changeLink AS changeLink, t1.changeBranch AS changeBranch, t1.changeTag AS changeTag, t1.changeStatus AS changeStatus, t2.message AS changeMessage FROM scm_activity "
                    + "t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID WHERE "
                    + "t1.issueKey=? AND t1.changeId=? AND t1.changeType=?";
            
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, issueKey);
            statement.setString(2, changeId);
            statement.setString(3, changeType);
            statement.setMaxRows(1);
            
            resultSet = statement.executeQuery();            

            if( resultSet.next() ) {
                scmBean = new ScmActivityBean();
                scmBean.setId( resultSet.getLong("ID") );                
                scmBean.setIssueKey(resultSet.getString("issueKey") );
                scmBean.setChangeId( resultSet.getString("changeId") );
                scmBean.setChangeType( resultSet.getString("changeType") );
                scmBean.setChangeAuthor( resultSet.getString("changeAuthor") );
                scmBean.setChangeDate( resultSet.getString("changeDate") );
                scmBean.setChangeBranch( resultSet.getString("changeBranch") );
                scmBean.setChangeTag( resultSet.getString("changeTag") );
                scmBean.setChangeStatus( resultSet.getString("changeStatus") );
                scmBean.setChangeLink( resultSet.getString("changeLink") );
                scmBean.setChangeMessage(scmBean.getChangeMessage());
                                                
                //look for if any change files
                List<ScmFileBean> files = ScmFileServiceImpl.getInstance().getScmFiles(scmBean.getId(), connection);
                scmBean.setChangeFiles(files);
                    
                //look for if any jobs
                List<ScmJobBean> jobs = ScmJobServiceImpl.getInstance().getScmJobs(scmBean.getId(), connection);
                scmBean.setJobs(jobs);
                
            }                                    
        } catch(SQLException ex) {
            LOGGER.error(ex);
        }
        finally {
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
                if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        return scmBean;
    }    
    
    
    /**
     * Method to get SCM Activity ID by Issue Key, Change Id, and Change Type
     * Used many places to check SCM activity existence 
     * @param issueKey
     * @param changeId
     * @param changeType
     * @param connection
     * @return 
     */
    
    @Override
    public long getScmActivityID(String issueKey, String changeId, String changeType, Connection connection) {        
        long scmActivityID = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try{          
            String QUERY = "SELECT ID FROM scm_activity WHERE issueKey=? AND changeId=? AND changeType=?";
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, issueKey.trim());
            statement.setString(2, changeId.trim());
            statement.setString(3, changeType.trim());
            resultSet = statement.executeQuery();
            if( resultSet.next() ){
                scmActivityID = resultSet.getLong("ID");
                LOGGER.debug("SCM ID is found returning now: "+ scmActivityID);
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
        return scmActivityID;
    }        
    
    
    /**
     * Method to get One SCM Activity by Order
     * Used for Custom Field and REST API
     * @param issueKey
     * @param orderBy
     * @param style
     * @return 
     */
    
    @Override
    public ScmActivityCustomFieldBean getScmActivity(String issueKey, String orderBy, int style) {
        ScmActivityCustomFieldBean scmBean = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
                
        try {
            
            connection = ScmActivityDB.getInstance().getConnection();

            String QUERY = "SELECT t1.ID AS ID, t1.issueKey AS issueKey, t1.changeId AS changeId, t1.changeType AS changeType, t1.changeDate AS changeDate, t1.changeAuthor AS changeAuthor, "
                    + "t1.changeLink AS changeLink, t1.changeBranch AS changeBranch, t1.changeTag AS changeTag, t1.changeStatus AS changeStatus, t2.message AS changeMessage FROM scm_activity "
                    + "t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
                    + "WHERE t1.issueKey=? ORDER BY t1.changeDate "+orderBy;
            
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, issueKey);
            statement.setMaxRows(1);
            
            resultSet = statement.executeQuery();            

            if( resultSet.next() ) {
                scmBean = new ScmActivityCustomFieldBean();
                scmBean.setId( resultSet.getLong("ID") );                
                scmBean.setIssueKey(resultSet.getString("issueKey") );
                scmBean.setChangeId( resultSet.getString("changeId") );
                scmBean.setChangeType( resultSet.getString("changeType") );
                scmBean.setChangeAuthor( resultSet.getString("changeAuthor") );
                scmBean.setChangeDate( resultSet.getString("changeDate") );
                scmBean.setChangeBranch( resultSet.getString("changeBranch") );
                scmBean.setChangeTag( resultSet.getString("changeTag") );
                scmBean.setChangeStatus( resultSet.getString("changeStatus") );
                scmBean.setChangeLink( resultSet.getString("changeLink") );
                scmBean.setChangeMessage( resultSet.getString("changeMessage") );
                
                if( style == 1 ) {
                    scmBean.setChangeMessage(ScmActivityUtils.getInstance()
                        .getWikiText(scmBean.getChangeMessage()));
                
                
                    scmBean.setJiraAuthor(ScmActivityUtils.getInstance()
                            .getJiraAuthor(scmBean.getChangeAuthor()));


                    if( "git".equals(scmBean.getChangeType()) ) {
                        User user = ScmActivityUtils.getInstance().getJiraAuthor4Git(scmBean.getChangeAuthor());
                        if( user != null ) {
                            scmBean.setChangeAuthor(user.getName());
                            scmBean.setJiraAuthor(user.getDisplayName());
                        }
                    }
                }
                
                //look for if any change files
                List<ScmFileBean> files = ScmFileServiceImpl.getInstance().getScmFiles(scmBean.getId(), connection);
                scmBean.setChangeFiles(files);
                    
                //look for if any jobs
                List<ScmJobBean> jobs = ScmJobServiceImpl.getInstance().getScmJobs(scmBean.getId(), connection);
                scmBean.setJobs(jobs);
                
            }                                    
        } catch(SQLException ex) {
            LOGGER.error(ex);
        }
        finally {
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
                if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        return scmBean;
    }

    
    /**
     * Active Object to get SCM Activities by Issue Key
     * Used to display activities in Tab Panel
     * @param issueKey
     * @return 
     */    
    
    @Override
    public List<ScmActivityBean> getScmActivities(String issueKey) {
        List<ScmActivityBean> activities = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            
            connection = ScmActivityDB.getInstance().getConnection();

            String QUERY = "SELECT t1.ID AS ID, t1.issueKey AS issueKey, t1.changeId AS changeId, t1.changeType AS changeType, t1.changeDate AS changeDate, t1.changeAuthor AS changeAuthor, "
                    + "t1.changeLink AS changeLink, t1.changeBranch AS changeBranch, t1.changeTag AS changeTag, t1.changeStatus AS changeStatus, t2.message AS changeMessage FROM scm_activity "
                    + "t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
                    + "WHERE t1.issueKey=? ORDER BY t1.changeDate DESC";
            
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, issueKey);
            
            resultSet = statement.executeQuery();
            
            activities = new ArrayList<ScmActivityBean>();
                
            ScmActivityBean scmBean = null;

            while( resultSet.next() ) {
                scmBean = new ScmActivityBean();
                scmBean.setId( resultSet.getLong("ID") );                
                scmBean.setIssueKey(resultSet.getString("issueKey") );
                scmBean.setChangeId( resultSet.getString("changeId") );
                scmBean.setChangeType( resultSet.getString("changeType") );
                scmBean.setChangeAuthor( resultSet.getString("changeAuthor") );
                scmBean.setChangeDate( resultSet.getString("changeDate") );
                scmBean.setChangeBranch( resultSet.getString("changeBranch") );
                scmBean.setChangeTag( resultSet.getString("changeTag") );
                scmBean.setChangeStatus( resultSet.getString("changeStatus") );
                scmBean.setChangeLink( resultSet.getString("changeLink") );
                scmBean.setChangeMessage( resultSet.getString("changeMessage") );
                    
                //look for if any change files
                List<ScmFileBean> files = ScmFileServiceImpl.getInstance().getScmFiles(scmBean.getId(), connection);
                scmBean.setChangeFiles(files);
                    
                //look for if any jobs
                List<ScmJobBean> jobs = ScmJobServiceImpl.getInstance().getScmJobs(scmBean.getId(), connection);
                scmBean.setJobs(jobs);

                activities.add(scmBean);
                
            }
            
        } catch (SQLException e) {                    
            LOGGER.error(e.getLocalizedMessage());
        }
        finally {
            try {
                if( resultSet != null ) resultSet.close();
                if( statement != null ) statement.close();
                if( connection != null ) connection.close();
            } catch (SQLException ex) {
                LOGGER.error(ex);
            }
        }
        
        return activities;
    }
    

    @Override
    public ScmActivityNotifyBean getScmActivityToNotify(String issueKey, String changeId, String changeType) {
        ScmActivityNotifyBean scmBean = null;
        ScmActivityBean activity = getScmActivity(issueKey, changeId, changeType);
        if( activity != null ) {
            scmBean = new ScmActivityNotifyBean();
            scmBean.setId(activity.getId());
            scmBean.setIssueKey(activity.getIssueKey());
            scmBean.setChangeId(activity.getChangeId());
            scmBean.setChangeType(activity.getChangeType());
            scmBean.setChangeAuthor(activity.getChangeAuthor());
            scmBean.setChangeDate(activity.getChangeDate());
            scmBean.setChangeBranch(activity.getChangeBranch());
            scmBean.setChangeTag(activity.getChangeTag());
            scmBean.setChangeStatus(activity.getChangeStatus());
            scmBean.setChangeLink(activity.getChangeLink());
            
            //message
            if(activity.getChangeMessage()!=null) {
                scmBean.setChangeMessage( ScmActivityUtils.getInstance()
                        .getWikiText(activity.getChangeMessage()) );
                
                scmBean.setChangeMsgNonWiki(activity.getChangeMessage());
            }
            
            //affected files
            if( activity.getChangeFiles().size() > 0 ) {
                List<ScmFileBean> changeFiles = new ArrayList<ScmFileBean>();
                for(ScmFileBean file : activity.getChangeFiles()){
                    ScmFileBean fileBean = new ScmFileBean();
                    fileBean.setId(file.getId());
                    fileBean.setFileName(file.getFileName());
                    fileBean.setFileAction(file.getFileAction());
                    fileBean.setFileVersion(file.getFileVersion());
                    changeFiles.add(fileBean);
                }
                scmBean.setChangeFiles(changeFiles);
            }
            
            //jobs
            if( activity.getJobs().size() > 0 ) {
                List<ScmJobBean> changeJobs = new ArrayList<ScmJobBean>();
                for(ScmJobBean job : activity.getJobs()){
                    ScmJobBean jobBean = new ScmJobBean();
                    jobBean.setId(job.getId());
                    jobBean.setJobName(job.getJobName());
                    jobBean.setJobStatus(job.getJobStatus());
                    jobBean.setJobLink(job.getJobLink());
                    changeJobs.add(jobBean);
                }
                scmBean.setJobs(changeJobs);
            }
        }
        return scmBean;
    }
    
    
    /**
     * Method to search SCM activities with JQL function scmSearch()
     * @param stext
     * @return 
     */

    @Override
    public List<String> getScmActivitiesSearch(String stext) {
        List<String> issueKeys = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            String s = "%"+stext+"%";
            String QUERY = "SELECT t1.issueKey AS issueKey FROM scm_activity t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
                + "LEFT JOIN scm_files t3 ON t1.ID=t3.scmActivityID LEFT JOIN scm_job t4 ON t1.ID=t4.scmActivityID WHERE "
                + "( t1.changeId LIKE ? OR t1.changeType LIKE ? OR t1.changeBranch LIKE ? OR t1.changeAuthor LIKE ? OR "
                + "t1.changeTag LIKE ? OR t1.changeStatus LIKE ? OR t2.message LIKE ? OR t3.fileName LIKE ? OR "
                + "t4.jobName LIKE ? ) GROUP BY t1.issueKey";
            
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, s);
            statement.setString(2, s);
            statement.setString(3, s);
            statement.setString(4, s);
            statement.setString(5, s);
            statement.setString(6, s);
            statement.setString(7, s);
            statement.setString(8, s);
            statement.setString(9, s);
            statement.setMaxRows(1000);
            
            resultSet = statement.executeQuery();
            
            if( resultSet != null ) {
                issueKeys = new ArrayList<String>();
                while ( resultSet.next() ) {
                    issueKeys.add(resultSet.getString("issueKey"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e);
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
        return issueKeys;    
    }

    /**
     * Method to search SCM activities with JQL function scmActivitySearch()
     * @param changeAuthor
     * @param changeType
     * @param changeBranch
     * @param changeTag
     * @param changeStatus
     * @param startDate
     * @param stext
     * @return 
     */
    
    @Override
    public List<String> getScmActivitiesSearch(String startDate, String changeType, String changeAuthor, 
            String changeBranch, String changeTag, String changeStatus, String stext) {
        
        List<String> issueKeys = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            String sauthor = "%"+changeAuthor+"%";
            String stype = "%"+changeType+"%";
            String s = "%"+stext+"%";
            
            String QUERY = "SELECT t1.issueKey AS issueKey FROM scm_activity t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
                + "LEFT JOIN scm_files t3 ON t1.ID=t3.scmActivityID LEFT JOIN scm_job t4 ON t1.ID=t4.scmActivityID WHERE "
                + "t1.changeType LIKE ? AND t1.changeAuthor LIKE ? AND ( t2.message LIKE ? OR t3.fileName LIKE ? OR "
                + "t4.jobName LIKE ? )";
            
            if( changeBranch != null && !"".equals(changeBranch) ) {
                QUERY += " AND t1.changeBranch LIKE '%"+changeBranch+"%'";
            }
            
            if( changeTag != null && !"".equals(changeTag) ) {
                QUERY += " AND t1.changeTag LIKE '%"+changeTag+"%'";
            }
            
            if( changeStatus != null && !"".equals(changeStatus) ) {
                QUERY += " AND t1.changeStatus LIKE '%"+changeStatus+"%'";
            }
            
            if( startDate != null && !"".equals(startDate) ) {
                QUERY += " AND t1.changeDate > '"+startDate+"'";
            }
            
            QUERY += " GROUP BY t1.issueKey";
            
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, stype);
            statement.setString(2, sauthor);
            statement.setString(3, s);
            statement.setString(4, s);
            statement.setString(5, s);
            statement.setMaxRows(1000);
            
            resultSet = statement.executeQuery();
            
            if( resultSet != null ) {
                issueKeys = new ArrayList<String>();
                while ( resultSet.next() ) {
                    issueKeys.add(resultSet.getString("issueKey"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e);
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
        return issueKeys;
    }
    
}
