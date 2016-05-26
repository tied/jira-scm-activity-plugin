/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;

/**
 *
 * @author vprasad
 */
public interface ScmMessageService {
    
    ScmMessageBean setScmMessage(String message, long scmActivityID, Connection connection);
    
    ScmMessageBean setScmMessage(ScmChangeSetBean activityBean);
    
    void deleteScmMessage(long scmActivityID, Connection connection);
    
    ScmMessageBean getScmMessage(long scmActivityID, Connection connection);
}
