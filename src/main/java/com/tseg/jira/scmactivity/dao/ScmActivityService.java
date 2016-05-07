/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.model.ScmActivityBean;
import com.tseg.jira.scmactivity.model.ScmActivityCustomFieldBean;
import com.tseg.jira.scmactivity.model.ScmActivityNotifyBean;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.util.List;

/**
 *
 * @author vprasad
 */
public interface ScmActivityService {
    
    ScmMessageBean setScmActivity(ScmChangeSetBean activityBean);    
    
    void deleteScmActivity(ScmActivity scmActivity);        
    
    ScmActivityCustomFieldBean getScmActivityBean(String issueKey, String changeId, String changeType);
    
    ScmActivity getScmActivity(String issueKey, String changeId, String changeType);
    
    ScmActivityCustomFieldBean getScmActivity(String issueKey, String order, int style);
    
    ScmActivityNotifyBean getScmActivityToNotify(String issuekey, String changeId, String changeType);
    
    ScmActivity[] getScmActivities(String issueKey);

    ScmActivity[] getScmActivitiesSearch(String changeText);
    
    ScmActivity[] getScmActivitiesSearch(String changeAuthor, String changeType, 
            String changeBranch, String changeTag, String changeStatus, String startDate, String changeText);
            
    List<ScmActivityBean> getScmActivityList(String issueKey);
}
