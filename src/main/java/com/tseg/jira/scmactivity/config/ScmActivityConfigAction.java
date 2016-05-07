package com.tseg.jira.scmactivity.config;

import com.tseg.jira.scmactivity.model.ScmActivityConfigBean;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.log4j.Logger;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import net.java.ao.EntityManager;

/**
 * @author vprasad
 */
public class ScmActivityConfigAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ScmActivityConfigAction.class);
    private final ScmActivityConfigMgr scmConfigMgr;
    private ScmActivityConfigBean configBean = new ScmActivityConfigBean();
    private String submitted;
    private String status;
    private long index = 0;
    
    public ScmActivityConfigAction(ScmActivityConfigMgr scmConfigMgr) {
        this.scmConfigMgr = scmConfigMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasPermission(0) ) {
            return "error";
        }
        if (this.submitted == null) {
            LOGGER.debug("Loading scm configuration settings.");
            configBean = scmConfigMgr.getScmConfigProperties();
        } else {
            LOGGER.debug("Saving scm configuration settings.");
            scmConfigMgr.setScmConfigProperties(configBean);

            LOGGER.debug("Testing scm entity manager configuration settings.");
            
            ScmActivityEntityManager.getInstance().inValidateEntityManager();
            
            EntityManager entityManager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            if( entityManager != null ) {
                ScmMessageBean bean = ScmActivityEntityManager.getInstance().migrateEntities();
                status = bean.getMessage();
                index = bean.getResult();
            }else{
                status = "Entity Manager is NULL. Connection Failed.";
                index = 0;
            }            
        }
        return "success";
    }

    public String getDb_user() {
        return configBean.getDb_user();
    }

    public void setDb_user(String db_user) {
        configBean.setDb_user(db_user);
    }

    public String getDb_pass() {
        return configBean.getDb_pass();
    }

    public void setDb_pass(String db_pass) {
        configBean.setDb_pass(db_pass);
    }
    
    public String getDb_url() {
        return configBean.getDb_url();
    }

    public void setDb_url(String db_url) {
        configBean.setDb_url(db_url);
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
    
    public long getIndex() {
        return index;
    }
}
