/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tse.jira.scmactivity.dao;

import com.tse.jira.scmactivity.model.ScmChangeSetBean;
import com.tse.jira.scmactivity.model.ScmMessageBean;
import java.sql.Connection;

/**
 *
 * @author scmenthusiast@gmail.com
 */
public interface ScmMessageService {
    
    ScmMessageBean setScmMessage(String message, long scmActivityID, Connection connection);
    
    ScmMessageBean setScmMessage(ScmChangeSetBean activityBean);
    
    void deleteScmMessage(long scmActivityID, Connection connection);
    
    ScmMessageBean getScmMessage(long scmActivityID, Connection connection);
}
