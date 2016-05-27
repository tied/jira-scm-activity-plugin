/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.model.ScmJobBean;
import com.tseg.jira.scmactivity.model.ScmJobLinkBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author vprasad
 */
public interface ScmJobService {
    
    ScmMessageBean setScmJob(ScmJobLinkBean jobBean);
    
    ScmJobBean getScmJob(String issueKey, String changeId, String changeType, String jobName);
    
    long getScmJobId(long scmActivityID, String jobName, Connection connection);
    
    //List<ScmJobBean> getScmJobs(String issueKey, String changeId, String changeType);
    
    List<ScmJobBean> getScmJobs(long scmActivityID, Connection connection);        
    
    void deleteScmJobs(long scmActivityID, Connection connection);    
    
    void deleteScmJob(String issueKey, String changeId, String changeType, long jobId);
}
