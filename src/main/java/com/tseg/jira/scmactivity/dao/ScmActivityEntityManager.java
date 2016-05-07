/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tseg.jira.scmactivity.config.ScmActivityConfigMgr;
import com.tseg.jira.scmactivity.config.ScmActivityOptionMgr;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmFile;
import com.tseg.jira.scmactivity.dao.entities.ScmJob;
import com.tseg.jira.scmactivity.dao.entities.ScmMessage;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.sql.SQLException;
import net.java.ao.EntityManager;
import org.apache.log4j.Logger;


/**
 *
 * @author vprasad
 */
public class ScmActivityEntityManager {
    
    private static final Logger LOGGER = Logger.getLogger(ScmActivityEntityManager.class);
    private static ScmActivityEntityManager scmActivityEntityManager = null;
    private static EntityManager entityManager = null;
    public static long customEventId = 0;
    public static long expandCount = 5;
    
    private ScmActivityEntityManager() { }

    public static ScmActivityEntityManager getInstance() {
        if ( scmActivityEntityManager == null ) {
            scmActivityEntityManager = new ScmActivityEntityManager();
        }
        return scmActivityEntityManager;
    }
    
    public EntityManager getEntityManager() {
        if( entityManager == null ) {            
            LOGGER.debug("[Debug] Entity Manager Object is NULL so creating one.");

            ApplicationProperties props = ComponentAccessor.getApplicationProperties();
            
            String db_user = props.getString(ScmActivityConfigMgr.DB_USER);
            String db_pass = props.getString(ScmActivityConfigMgr.DB_PASS);
            String db_url = props.getString(ScmActivityConfigMgr.DB_URL);
            
            String jira_event_id = props.getString(ScmActivityOptionMgr.JIRA_EVENT_ID);
            String expand_count = props.getString(ScmActivityOptionMgr.EXPAND_COUNT);
        
            if( !"".equals(db_user) && !"".equals(db_pass) && !"".equals(db_url) ) {
                entityManager = new EntityManager(db_url, db_user, db_pass);
            }
            
            if( jira_event_id != null && !"".equals(jira_event_id) ){
                customEventId = Long.parseLong(jira_event_id);
            }
            
            if( expand_count != null && !"".equals(expand_count) ){
                expandCount = Long.parseLong(expand_count);
            }
            
        }
        return entityManager;
    }
    
    public ScmMessageBean migrateEntities() {
        ScmMessageBean messageBean = new ScmMessageBean();
        int result = 0;
        try {
            LOGGER.debug("[Debug] Migrating Entities.");
            getEntityManager().migrate(ScmActivity.class, ScmMessage.class, ScmFile.class, ScmJob.class);
            messageBean.setResult( 1 );
            messageBean.setMessage("[Info] Active Objects Schema initialized.");
        } catch (SQLException ex) {
            LOGGER.error(ex);
            messageBean.setResult(result);
            messageBean.setMessage("Active Objects Schema initialize failed. "
                    + "See <a href=\"https://github.com/the-scm-enthusiast-group/jira-scm-activity-plugin/wiki/JIRA-SCM-Activity-Database-Set-Up\">SCM Activity Database Set-Up</a>");
            return messageBean;
        }
        return messageBean;
    }
    
    
    public void inValidateEntityManager() {
        entityManager = null;
    }
}
