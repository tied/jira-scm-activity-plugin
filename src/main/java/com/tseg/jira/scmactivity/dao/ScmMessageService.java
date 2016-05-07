/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmMessage;
import com.tseg.jira.scmactivity.model.ScmMessageBean;

/**
 *
 * @author vprasad
 */
public interface ScmMessageService {
    ScmMessageBean setScmMessage(String message, ScmActivity scmActivity);
    
    void deleteScmMessage(ScmMessage scmMessage);
    
    ScmMessage getScmMessage(long scmId);
}
