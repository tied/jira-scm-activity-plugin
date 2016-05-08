/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import com.atlassian.crowd.embedded.api.User;
import java.sql.SQLException;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityService;
import com.tseg.jira.scmactivity.dao.ScmFileService;
import com.tseg.jira.scmactivity.dao.ScmMessageService;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmFile;
import com.tseg.jira.scmactivity.dao.entities.ScmJob;
import com.tseg.jira.scmactivity.dao.entities.ScmMessage;
import com.tseg.jira.scmactivity.model.ScmActivityBean;
import com.tseg.jira.scmactivity.model.ScmActivityCustomFieldBean;
import com.tseg.jira.scmactivity.model.ScmActivityNotifyBean;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmFileBean;
import com.tseg.jira.scmactivity.model.ScmJobBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import com.tseg.jira.scmactivity.plugin.ScmActivityUtils;
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
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            ScmMessageService messageService = ScmMessageServiceImpl.getInstance();
            ScmFileService fileService = ScmFileServiceImpl.getInstance();
            ScmActivity scmActivity = getScmActivity(activityBean.getIssueKey(), 
                    activityBean.getChangeId(), activityBean.getChangeType());
            
            if( scmActivity == null ) {
                final ScmActivity newScmActivity =  entityManager.create(ScmActivity.class);
                newScmActivity.setIssueKey(activityBean.getIssueKey());
                newScmActivity.setChangeId(activityBean.getChangeId());
                newScmActivity.setChangeType(activityBean.getChangeType());
                newScmActivity.setChangeAuthor(activityBean.getChangeAuthor());
                newScmActivity.setChangeDate(activityBean.getChangeDate());
                newScmActivity.setChangeBranch(activityBean.getChangeBranch());
                newScmActivity.setChangeLink(activityBean.getChangeLink());
                newScmActivity.setChangeTag(activityBean.getChangeTag());
                newScmActivity.setChangeStatus(activityBean.getChangeStatus());
                newScmActivity.save();
                
                if( activityBean.getChangeMessage() != null && !"".equals(activityBean.getChangeMessage()) ) {
                    messageService.setScmMessage(activityBean.getChangeMessage(), newScmActivity);
                }
                
                if( activityBean.getChangeFiles() != null && !activityBean.getChangeFiles().isEmpty() ) {
                    fileService.setScmFiles(activityBean.getChangeFiles(), newScmActivity);
                }
                
                messageBean.setResult(1);
                messageBean.setMessage("[Info] "+activityBean.getIssueKey() +" > "+activityBean.getChangeId()+""
                        + " activity row ["+newScmActivity.getID()+"] is added.");
                    
            } else {
                
                if( activityBean.getChangeUpdate() == true ) {
                    
                    scmActivity.setChangeAuthor(activityBean.getChangeAuthor());
                    scmActivity.setChangeDate(activityBean.getChangeDate());
                    scmActivity.setChangeLink(activityBean.getChangeLink());
                    scmActivity.setChangeBranch(activityBean.getChangeBranch());
                    scmActivity.setChangeTag(activityBean.getChangeTag());
                    scmActivity.setChangeStatus(activityBean.getChangeStatus());
                    scmActivity.save();
                    
                    if( activityBean.getChangeMessage() != null && !"".equals(activityBean.getChangeMessage()) ) {
                        messageService.setScmMessage(activityBean.getChangeMessage(), scmActivity);
                    }
                    
                    if( activityBean.getChangeFiles() != null && !activityBean.getChangeFiles().isEmpty() ) {
                        fileService.setScmFiles(activityBean.getChangeFiles(), scmActivity);
                    }
                    
                    messageBean.setResult(1);
                    messageBean.setMessage("[Info] "+activityBean.getIssueKey() +" > "+activityBean.getChangeId()+""
                        + " activity row ["+scmActivity.getID()+"] is updated.");                    
                    
                } else {
                                        
                    messageBean.setResult(1);
                    messageBean.setMessage("[Error] Skipping. There is "+activityBean.getChangeType() +" Id ["+activityBean.getChangeId()+"]"
                        + " already exists on issue key ["+activityBean.getIssueKey()+"].");                    
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return messageBean;
    }
    
    
    /**
     * Method to delete SCM Activity
     * @param scmActivity 
     */
    
    @Override
    public void deleteScmActivity(ScmActivity scmActivity) {
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            //jobs
            for(ScmJob scmJob : scmActivity.getScmJobs()){
                entityManager.delete(scmJob);
            }
            
            //files
            for(ScmFile scmFile : scmActivity.getChangeFiles()){
                entityManager.delete(scmFile);
            }
            
            //messages
            ScmMessage scmMessage = scmActivity.getChangeMessage();
            if( scmMessage !=null ){
                entityManager.delete(scmMessage);
            }
            
            entityManager.delete(scmActivity);
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
    }
    

    /**
     * Method to get SCM Activity by Issue Key, Change Id, and Change Type
     * Used many places to check SCM activity existence 
     * @param issueKey
     * @param changeId
     * @param changeType
     * @return 
     */
    
    @Override
    public ScmActivity getScmActivity(String issueKey, String changeId, String changeType) {        
        try {
            
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] activities = manager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ? AND changeId = ? AND changeType = ?", issueKey, changeId, changeType));
            
            return activities.length > 0 ? activities[0] : null;
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }
    
    
    @Override
    public ScmActivityCustomFieldBean getScmActivityBean(String issueKey, String changeId, String changeType) {        
        try {
            
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] activities = entityManager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ? AND changeId = ? AND changeType = ?", issueKey, changeId, changeType));
            
            if( activities.length > 0 ) {
                ScmActivity activity = activities[0];
                ScmActivityCustomFieldBean scmBean = new ScmActivityCustomFieldBean();
                scmBean.setScmId(activity.getID());
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
                    scmBean.setChangeMessage(activity.getChangeMessage().getMessage());
                }
                    
                //affected files
                if( activity.getChangeFiles().length > 0 ) {
                    List<ScmFileBean> changeFiles = new ArrayList<ScmFileBean>();
                    for(ScmFile file : activity.getChangeFiles()){
                        ScmFileBean fileBean = new ScmFileBean();
                        fileBean.setId(file.getID());
                        fileBean.setFileName(file.getFileName());
                        fileBean.setFileAction(file.getFileAction());
                        fileBean.setFileVersion(file.getFileVersion());
                        changeFiles.add(fileBean);
                    }
                    scmBean.setChangeFiles(changeFiles);
                }
                    
                //jobs
                if( activity.getScmJobs().length > 0 ) {
                    List<ScmJobBean> changeJobs = new ArrayList<ScmJobBean>();
                    for(ScmJob job : activity.getScmJobs()){
                        ScmJobBean jobBean = new ScmJobBean();
                        jobBean.setJobId(job.getID());
                        jobBean.setJobName(job.getJobName());
                        jobBean.setJobStatus(job.getJobStatus());
                        jobBean.setJobLink(job.getJobLink());
                        changeJobs.add(jobBean);
                    }
                    scmBean.setScmJobs(changeJobs);
                }
                
                return scmBean;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
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
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] activities = entityManager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ?", issueKey).order("changeDate "+ orderBy));
            
            if( activities.length > 0 ) {
                ScmActivity activity = activities[0];
                ScmActivityCustomFieldBean scmBean = new ScmActivityCustomFieldBean();
                scmBean.setScmId(activity.getID());
                scmBean.setIssueKey(activity.getIssueKey());
                scmBean.setChangeId(activity.getChangeId());
                scmBean.setChangeType(activity.getChangeType());
                scmBean.setChangeAuthor(activity.getChangeAuthor());
                scmBean.setChangeDate(activity.getChangeDate());
                scmBean.setChangeBranch(activity.getChangeBranch());
                scmBean.setChangeTag(activity.getChangeTag());
                scmBean.setChangeStatus(activity.getChangeStatus());
                scmBean.setChangeLink(activity.getChangeLink());
                
                scmBean.setJiraAuthor(ScmActivityUtils.getInstance()
                        .getJiraAuthor(activity.getChangeAuthor()));
                
                if( "git".equals(activity.getChangeType()) ) {
                    User user = ScmActivityUtils.getInstance().getJiraAuthor4Git(activity.getChangeAuthor());
                    if( user != null ) {
                        scmBean.setChangeAuthor(user.getName());
                        scmBean.setJiraAuthor(user.getDisplayName());
                    }
                }
                
                //message
                if(activity.getChangeMessage()!=null) {
                    if(style==1) {
                        scmBean.setChangeMessage(ScmActivityUtils.getInstance()
                            .getWikiText(activity.getChangeMessage().getMessage()));
                    }else{
                        scmBean.setChangeMessage(activity.getChangeMessage().getMessage());
                    }
                }
                    
                //affected files
                if( activity.getChangeFiles().length > 0 ) {
                    List<ScmFileBean> changeFiles = new ArrayList<ScmFileBean>();
                    for(ScmFile file : activity.getChangeFiles()){
                        ScmFileBean fileBean = new ScmFileBean();
                        fileBean.setId(file.getID());
                        fileBean.setFileName(file.getFileName());
                        fileBean.setFileAction(file.getFileAction());
                        fileBean.setFileVersion(file.getFileVersion());
                        changeFiles.add(fileBean);
                    }
                    scmBean.setChangeFiles(changeFiles);
                }
                    
                //jobs
                if( activity.getScmJobs().length > 0 ) {
                    List<ScmJobBean> changeJobs = new ArrayList<ScmJobBean>();
                    for(ScmJob job : activity.getScmJobs()){
                        ScmJobBean jobBean = new ScmJobBean();
                        jobBean.setJobId(job.getID());
                        jobBean.setJobName(job.getJobName());
                        jobBean.setJobStatus(job.getJobStatus());
                        jobBean.setJobLink(job.getJobLink());
                        changeJobs.add(jobBean);
                    }
                    scmBean.setScmJobs(changeJobs);
                }
                
                return scmBean;
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }

    
    /**
     * Active Object to get SCM Activities by Issue Key
     * Used to display activities in Tab Panel
     * @param issueKey
     * @return 
     */
    
    @Override
    public ScmActivity[] getScmActivities(String issueKey) {
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            return entityManager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ?", issueKey).order("changeDate DESC"));
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }

    
    /**
     * Method to get SCM activities by Issue Key with Model Bean
     * Used for REST API interface
     * @param issueKey
     * @return 
     */
    
    @Override
    public List<ScmActivityBean> getScmActivityList(String issueKey) {
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            ScmActivity[] scmActvities = entityManager.find(ScmActivity.class, Query.select()
                    .where("issueKey = ?", issueKey));
            
            if( scmActvities.length > 0 ) {
                
                List<ScmActivityBean> changeActivities = new ArrayList<ScmActivityBean>();
                ScmActivityBean scmBean = null;
                
                for(ScmActivity activity : scmActvities) {
                    scmBean = new ScmActivityBean();
                    scmBean.setScmId(activity.getID());
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
                        scmBean.setChangeMessage(activity.getChangeMessage().getMessage());
                    }
                    
                    //affected files
                    if( activity.getChangeFiles().length > 0 ) {
                        List<ScmFileBean> changeFiles = new ArrayList<ScmFileBean>();
                        for(ScmFile file : activity.getChangeFiles()){
                            ScmFileBean fileBean = new ScmFileBean();
                            fileBean.setId(file.getID());
                            fileBean.setFileName(file.getFileName());
                            fileBean.setFileAction(file.getFileAction());
                            fileBean.setFileVersion(file.getFileVersion());
                            changeFiles.add(fileBean);
                        }
                        scmBean.setChangeFiles(changeFiles);
                    }
                    
                    //jobs
                    if( activity.getScmJobs().length > 0 ) {
                        List<ScmJobBean> changeJobs = new ArrayList<ScmJobBean>();
                        for(ScmJob job : activity.getScmJobs()){
                            ScmJobBean jobBean = new ScmJobBean();
                            jobBean.setJobId(job.getID());
                            jobBean.setJobName(job.getJobName());
                            jobBean.setJobStatus(job.getJobStatus());
                            jobBean.setJobLink(job.getJobLink());
                            changeJobs.add(jobBean);
                        }
                        scmBean.setJobs(changeJobs);
                    }
                    
                    changeActivities.add(scmBean);
                    
                }
                
                return changeActivities;
            }
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public ScmActivityNotifyBean getScmActivityToNotify(String issueKey, String changeId, String changeType) {        
        ScmActivity activity = getScmActivity(issueKey, changeId, changeType);
        if( activity != null ) {
            ScmActivityNotifyBean scmBean = new ScmActivityNotifyBean();
            scmBean.setScmId(activity.getID());
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
                        .getWikiText(activity.getChangeMessage().getMessage()) );
                
                scmBean.setChangeMsgNonWiki(activity.getChangeMessage().getMessage());
            }
            
            //affected files
            if( activity.getChangeFiles().length > 0 ) {
                List<ScmFileBean> changeFiles = new ArrayList<ScmFileBean>();
                for(ScmFile file : activity.getChangeFiles()){
                    ScmFileBean fileBean = new ScmFileBean();
                    fileBean.setId(file.getID());
                    fileBean.setFileName(file.getFileName());
                    fileBean.setFileAction(file.getFileAction());
                    fileBean.setFileVersion(file.getFileVersion());
                    changeFiles.add(fileBean);
                }
                scmBean.setChangeFiles(changeFiles);
            }
            
            //jobs
            if( activity.getScmJobs().length > 0 ) {
                List<ScmJobBean> changeJobs = new ArrayList<ScmJobBean>();
                for(ScmJob job : activity.getScmJobs()){
                    ScmJobBean jobBean = new ScmJobBean();
                    jobBean.setJobId(job.getID());
                    jobBean.setJobName(job.getJobName());
                    jobBean.setJobStatus(job.getJobStatus());
                    jobBean.setJobLink(job.getJobLink());
                    changeJobs.add(jobBean);
                }
                scmBean.setJobs(changeJobs);
            }
            
            return scmBean;
        }
        return null;
    }
    
    /**
     * Method to find entity table name for given Class name
     * @param clazz
     * @return 
     */
    private String getTableName(Class clazz){
        EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
        return entityManager.getTableNameConverter().getName(clazz);
    }
    
    /**
     * Method to search SCM activities with JQL function scmSearch()
     * @param stext
     * @return 
     */

    @Override
    public ScmActivity[] getScmActivitiesSearch(String stext) {
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            String s = "%"+stext+"%";
            String QUERY = "SELECT t1.ID, t1.issueKey FROM scm_activity t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
                + "LEFT JOIN scm_files t3 ON t1.ID=t3.scmActivityID LEFT JOIN scm_job t4 ON t1.ID=t4.scmActivityID WHERE "
                + "( t1.changeId LIKE ? OR t1.changeType LIKE ? OR t1.changeBranch LIKE ? OR t1.changeAuthor LIKE ? OR "
                + "t1.changeTag LIKE ? OR t1.changeStatus LIKE ? OR t2.message LIKE ? OR t3.fileName LIKE ? OR "
                + "t4.jobName LIKE ? ) GROUP BY t1.issueKey LIMIT 1000";
            return entityManager.findWithSQL(ScmActivity.class, "t1.ID", QUERY, s, s, s, s, s, s, s, s, s);
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;    
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
    public ScmActivity[] getScmActivitiesSearch(String startDate, String changeType, String changeAuthor, 
            String changeBranch, String changeTag, String changeStatus, String stext) {        
        try {
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            String sauthor = "%"+changeAuthor+"%";
            String stype = "%"+changeType+"%";
            String s = "%"+stext+"%";
            
            String QUERY = "SELECT t1.ID, t1.issueKey FROM scm_activity t1 LEFT JOIN scm_message t2 ON t1.ID=t2.scmActivityID "
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
            
            QUERY += " GROUP BY t1.issueKey LIMIT 1000";
            
            return entityManager.findWithSQL(ScmActivity.class, "t1.ID", QUERY, 
                    stype, sauthor, s, s, s);
            
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }
    
}
