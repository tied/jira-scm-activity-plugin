/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import java.sql.SQLException;
import net.java.ao.EntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityService;
import com.tseg.jira.scmactivity.dao.ScmMessageService;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmMessage;
import com.tseg.jira.scmactivity.model.ScmChangeSetBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import net.java.ao.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmMessageServiceImpl implements ScmMessageService {

    private static final Logger LOGGER = Logger.getLogger(ScmMessageServiceImpl.class);
    private static ScmMessageServiceImpl scmMessageService = null;
        
    private ScmMessageServiceImpl() { }

    public static ScmMessageServiceImpl getInstance() {
        if ( scmMessageService == null ) {
            scmMessageService = new ScmMessageServiceImpl();
        }
        return scmMessageService;
    }
    
    @Override
    public ScmMessageBean setScmMessage(String changeMessage, ScmActivity scmActivity) {
        ScmMessageBean messageBean = new ScmMessageBean();
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            if( scmActivity != null && scmActivity.getID() != 0 ) {                
                ScmMessage scmMessage = getScmMessage(scmActivity.getID());                
                if(scmMessage == null) {
                    ScmMessage newScmMessage = manager.create(ScmMessage.class);
                    newScmMessage.setMessage(changeMessage);
                    newScmMessage.setScmActivity(scmActivity);
                    newScmMessage.save();
                    
                    messageBean.setResult(1);
                    messageBean.setMessage("[Info] "+scmActivity.getIssueKey() +" > "+scmActivity.getChangeId()+""
                        + " activity message row ["+newScmMessage.getID()+"] is added.");
                    
                } else {
                    scmMessage.setMessage(changeMessage);
                    scmMessage.save();
                    
                    messageBean.setResult(1);
                    messageBean.setMessage("[Info] "+scmActivity.getIssueKey() +" > "+scmActivity.getChangeId()+""
                        + " activity message row ["+scmMessage.getID()+"] is updated.");
                }
            } 
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            messageBean.setMessage(ex.getLocalizedMessage());
        }
        return messageBean;
    }

    @Override
    public void deleteScmMessage(ScmMessage scmMessage) {
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();            
            manager.delete(scmMessage);
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
    }
    
    @Override
    public ScmMessage getScmMessage(long scmId) {
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            ScmMessage[] scmMessages = manager.find(ScmMessage.class, Query.select()
                    .where("scmActivityID = ?", scmId));
            
            return scmMessages.length > 0 ? scmMessages[0] : null;
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }
    
}
