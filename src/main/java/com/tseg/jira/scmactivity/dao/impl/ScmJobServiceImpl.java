/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import com.tseg.jira.scmactivity.dao.ScmActivityDB;
import java.sql.SQLException;
import com.tseg.jira.scmactivity.dao.ScmJobService;
import com.tseg.jira.scmactivity.model.ScmJobBean;
import com.tseg.jira.scmactivity.model.ScmJobLinkBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
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
public class ScmJobServiceImpl implements ScmJobService {

    private static final Logger LOGGER = Logger.getLogger(ScmJobServiceImpl.class);
    private static ScmJobServiceImpl scmJobService = null;
        
    private ScmJobServiceImpl() { }

    public static ScmJobServiceImpl getInstance() {
        if ( scmJobService == null ) {
            scmJobService = new ScmJobServiceImpl();
        }
        return scmJobService;
    }
    
    @Override
    public ScmMessageBean setScmJob(ScmJobLinkBean jobBean) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(jobBean.getIssueKey(), jobBean.getChangeId(), 
                            jobBean.getChangeType(), connection);
            
            if( scmActivityID > 0 ) {
                
                long scmJobId = getScmJobId(scmActivityID, jobBean.getJobName(), connection);
                
                if( scmJobId == 0 ) {
                    String QUERY = "INSERT INTO scm_job (scmActivityID, jobName, jobLink, jobStatus) VALUES (?,?,?,?)";
                    statement = connection.prepareStatement(QUERY);
                    statement.setLong(1, scmActivityID);
                    statement.setString(2, jobBean.getJobName().trim());
                    statement.setString(3, jobBean.getJobLink().trim());
                    statement.setString(4, jobBean.getJobStatus().trim());
                    result = statement.executeUpdate();
                    
                    messageBean.setId(result);
                    messageBean.setMessage("[Info] "+jobBean.getIssueKey() +" > "+jobBean.getChangeId()+" > "
                            + jobBean.getJobName() +" joblink is added.");
                } else {
                    
                    if( jobBean.getJobUpdate() == true ) {
                        String QUERY = "UPDATE scm_job SET jobName=?, jobLink=?, jobStatus=? WHERE id=?";
                        statement = connection.prepareStatement(QUERY);                   
                        statement.setString(1, jobBean.getJobName().trim());
                        statement.setString(2, jobBean.getJobLink().trim());
                        statement.setString(3, jobBean.getJobStatus().trim());
                        statement.setLong(4, scmJobId);
                        result = statement.executeUpdate();
                        
                        messageBean.setId(result);
                        messageBean.setMessage("[Info] "+jobBean.getIssueKey() +" > "+jobBean.getChangeId()+" > "
                            + jobBean.getJobName() +" joblink is updated.");
                    
                    } else {
                        messageBean.setMessage("[Error] Skipping. There is job name "+jobBean.getJobName() +" already exists on"
                                + " Id ["+jobBean.getChangeId()+"] and issue key ["+jobBean.getIssueKey()+"].");
                    }
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
    
    @Override
    public ScmJobBean getScmJob(String issueKey, String changeId, String changeType, String jobName) {
        ScmJobBean scmJobBean = null;
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        
        try {
                    
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(issueKey, changeId, 
                            changeType, connection);
            
            String QUERY = "SELECT * FROM scm_job WHERE scmActivityID=? AND jobName=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            statement.setString(2, jobName);
            
            resultSet = statement.executeQuery();
            
            if( resultSet.next() ){
                scmJobBean = new ScmJobBean();
                scmJobBean.setId(resultSet.getLong("ID"));
                scmJobBean.setJobName(resultSet.getString("jobName"));
                scmJobBean.setJobStatus(resultSet.getString("jobStatus"));
                scmJobBean.setJobLink(resultSet.getString("jobLink"));
                LOGGER.debug("job id is found returning now - "+ scmJobBean.getId());
            }
            
        }
        catch(SQLException ex) {
            LOGGER.error(ex);
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
        return scmJobBean;
    }
    
    
    @Override
    public long getScmJobId(long scmActivityID, String jobName, Connection connection) {        
        long jobId = 0;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String QUERY = "SELECT ID FROM scm_job WHERE scmActivityID=? AND jobName=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            statement.setString(2, jobName);
            resultSet = statement.executeQuery();
            if( resultSet.next() ){
                jobId = resultSet.getLong("ID");
                LOGGER.debug("job ID is found returning now - "+ jobId);
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
        return jobId;
    }

    //@Override
    public List<ScmJobBean> getScmJobs(String issueKey, String changeId, String changeType) {
        ScmMessageBean messageBean = new ScmMessageBean();
        List<ScmJobBean> jobs = null;
        long result = 0;
        Connection connection = ScmActivityDB.getInstance().getConnection();
        long scmActivityID = ScmActivityServiceImpl.getInstance()
                .getScmActivityID(issueKey, changeId, changeType, connection);
        if ( scmActivityID == 0 ) {
            messageBean.setId(result);
            messageBean.setMessage("[Error] Scm "+changeType+" Id ["+changeId+"] not exists on issue key ["+issueKey+"].");
        } else {
            jobs = getScmJobs(scmActivityID, connection);
        }
        return jobs;
    }
    
    @Override
    public void deleteScmJob(String issueKey, String changeId, String changeType, long jobId) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(issueKey, changeId, 
                            changeType, connection);
            
            if(scmActivityID > 0) {
                String QUERY = "DELETE FROM scm_job WHERE scmActivityID=? AND ID=?";
                statement = connection.prepareStatement(QUERY);
                statement.setLong(1, scmActivityID);
                statement.setLong(2, jobId);

                result = statement.executeUpdate();

                messageBean.setId(result);
                messageBean.setMessage("[Info] joblink Id ["+jobId+"] is deleted for scm id ["+ scmActivityID +"].");
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
    public void deleteScmJobs(long scmActivityID, Connection connection) {
        ScmMessageBean messageBean = new ScmMessageBean();
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            String QUERY = "DELETE FROM scm_job WHERE scmActivityID=?";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            result = statement.executeUpdate();
            
            messageBean.setId(result);
            messageBean.setMessage("[Info] joblinks is deleted for scm id ["+ scmActivityID +"].");
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
    public void deleteScmJobs(String issueKey, String changeId, String changeType) {
        ScmMessageBean messageBean = new ScmMessageBean();
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        
        try {
            connection = ScmActivityDB.getInstance().getConnection();
            
            long scmActivityID = ScmActivityServiceImpl.getInstance()
                    .getScmActivityID(issueKey, changeId, 
                            changeType, connection);
            
            if(scmActivityID > 0) {
                String QUERY = "DELETE FROM scm_job WHERE scmActivityID=?";
                statement = connection.prepareStatement(QUERY);
                statement.setLong(1, scmActivityID);
                result = statement.executeUpdate();

                messageBean.setId(result);
                messageBean.setMessage("[Info] joblinks is deleted for scm id ["+ scmActivityID +"].");
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
    public List<ScmJobBean> getScmJobs(long scmActivityID, Connection connection) {
        List<ScmJobBean> jobs = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String QUERY = "SELECT * FROM scm_job WHERE scmActivityID=? ORDER BY id ASC";
            statement = connection.prepareStatement(QUERY);
            statement.setLong(1, scmActivityID);
            resultSet = statement.executeQuery();
            
            if( resultSet != null ) {
                jobs = new ArrayList<ScmJobBean>();
                ScmJobBean jobBean = null;
                while( resultSet.next() ){
                    jobBean = new ScmJobBean();
                    jobBean.setId( resultSet.getLong("ID") );
                    jobBean.setJobName( resultSet.getString("jobName") );
                    jobBean.setJobLink( resultSet.getString("jobLink") );
                    jobBean.setJobStatus( resultSet.getString("jobStatus") );
                    jobs.add(jobBean);
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
        return jobs;
    }
    
}
