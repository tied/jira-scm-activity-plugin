/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.model.ScmActivityBean;
import com.tseg.jira.scmactivity.model.ScmActivityCustomFieldBean;
import com.tseg.jira.scmactivity.model.ScmActivityNotifyBean;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author vprasad
 */
public interface ScmActivityService {
    
    ScmMessageBean setScmActivity(ScmChangeSetBean activityBean);    
    
    ScmMessageBean deleteScmActivity(String issueKey, String changeId, String changeType);
    
    ScmMessageBean deleteScmActivity(String issueKey);           
    
    long getScmActivityID(String issueKey, String changeId, String changeType, Connection connection);
    
    ScmActivityBean getScmActivity(String issueKey, String changeId, String changeType);
    
    ScmActivityCustomFieldBean getScmActivity(String issueKey, String order, int style);
    
    ScmActivityNotifyBean getScmActivityToNotify(String issuekey, String changeId, String changeType);
    
    List<ScmActivityBean> getScmActivities(String issueKey);

    List<String> getScmActivitiesSearch(String changeText);
    
    List<String> getScmActivitiesSearch(String changeAuthor, String changeType, 
            String changeBranch, String changeTag, String changeStatus, String startDate, String changeText);
}
