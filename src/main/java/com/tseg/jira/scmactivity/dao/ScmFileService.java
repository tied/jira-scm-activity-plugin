/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmFileBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author vprasad
 */
public interface ScmFileService {
    
    ScmMessageBean setScmFiles(List<ScmFileBean> changeFiles, long scmActivityID, Connection connection);
    
    ScmMessageBean setScmFiles(ScmChangeSetBean activityBean);
    
    List<ScmFileBean> getScmFiles(long scmActivityID, Connection connection);
    
    void deleteScmFiles(long scmActivityID, Connection connection);
}
