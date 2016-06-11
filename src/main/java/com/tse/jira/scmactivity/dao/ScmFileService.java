/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.scmactivity.dao;

import com.tse.jira.scmactivity.model.ScmChangeSetBean;
import com.tse.jira.scmactivity.model.ScmFileBean;
import com.tse.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author scmenthusiast@gmail.com
 */
public interface ScmFileService {
    
    ScmMessageBean setScmFiles(List<ScmFileBean> changeFiles, long scmActivityID, Connection connection);
    
    ScmMessageBean setScmFiles(ScmChangeSetBean activityBean);
    
    List<ScmFileBean> getScmFiles(long scmActivityID, Connection connection);        
    
    void deleteScmFile(String issueKey, String changeId, String changeType, long fileId);
    
    void deleteScmFiles(long scmActivityID, Connection connection);
}
