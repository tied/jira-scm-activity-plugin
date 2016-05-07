/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import java.sql.SQLException;
import net.java.ao.EntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityService;
import com.tseg.jira.scmactivity.dao.ScmJobService;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmJob;
import com.tseg.jira.scmactivity.model.ScmJobBean;
import com.tseg.jira.scmactivity.model.ScmJobLinkBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import net.java.ao.Query;
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
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();            
            ScmActivityService scmService = ScmActivityServiceImpl.getInstance();                        
            final ScmActivity scmActivity = scmService
                    .getScmActivity(jobBean.getIssueKey(), jobBean.getChangeId(), jobBean.getChangeType());
            
            if( scmActivity != null && scmActivity.getID() != 0 ) {
                
                ScmJob scmJob = getScmJob(jobBean.getIssueKey(), jobBean.getChangeId(), 
                        jobBean.getChangeType(), jobBean.getJobName());
                
                if( scmJob == null ) {
                    ScmJob scmJobNew = manager.create(ScmJob.class);
                    scmJobNew.setJobName(jobBean.getJobName());
                    scmJobNew.setJobStatus(jobBean.getJobStatus());
                    scmJobNew.setJobLink(jobBean.getJobLink());
                    scmJobNew.setScmActivity(scmActivity);
                    scmJobNew.save();
                    
                    messageBean.setResult(1);
                    messageBean.setMessage("[Info] "+jobBean.getIssueKey() +" > "+jobBean.getChangeId()+" > "
                            + jobBean.getJobName() +" joblink is added.");
                } else {
                    
                    if( jobBean.getJobUpdate() == true ) {
                        scmJob.setJobName(jobBean.getJobName());
                        scmJob.setJobStatus(jobBean.getJobStatus());
                        scmJob.setJobLink(jobBean.getJobLink());
                        scmJob.save();
                        
                        messageBean.setResult(1);
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
            messageBean.setMessage(ex.getLocalizedMessage());
        }
        return messageBean;
    }
    
    @Override
    public ScmJob getScmJob(String issueKey, String changeId, String changeType, String jobName) {        
        try {
            
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] activities = manager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ? AND changeId = ? AND changeType = ?", issueKey, changeId, changeType));
            
            if( activities.length > 0 ) {
                ScmActivity scmActivity = activities[0];
                
                ScmJob[] jobs = manager.find(ScmJob.class, Query.select()
                        .where("jobName = ? AND scmActivityID = ?", jobName, scmActivity.getID()));
                
                return jobs.length > 0 ? jobs[0] : null;
            }
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public ScmJob[] getScmJobs(String issueKey, String changeId, String changeType) {
        try {
            
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] activities = manager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ? AND changeId = ? AND changeType = ?", issueKey, changeId, changeType));
            
            if( activities.length > 0 ) {
                ScmActivity scmActivity = activities[0];
                
                ScmJob[] jobs = manager.find(ScmJob.class, Query.select()
                        .where("scmId = ?", scmActivity.getID()));
                
                return jobs;
            }
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }
    
    @Override
    public void deleteScmJob(ScmJob scmJob) {
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            manager.delete(scmJob);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    
}
