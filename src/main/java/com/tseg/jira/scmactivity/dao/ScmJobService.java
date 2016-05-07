/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.dao.entities.ScmJob;
import com.tseg.jira.scmactivity.model.ScmJobLinkBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;

/**
 *
 * @author vprasad
 */
public interface ScmJobService {
    
    ScmMessageBean setScmJob(ScmJobLinkBean jobBean);
    
    ScmJob getScmJob(String issueKey, String changeId, String changeType, String jobName);
    
    ScmJob[] getScmJobs(String issueKey, String changeId, String changeType);
    
    void deleteScmJob(ScmJob scmJob);
}
