package com.tse.jira.scmactivity.config;

import com.tse.jira.scmactivity.model.ScmActivityConfigBean;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.tse.jira.scmactivity.dao.ScmActivityDB;
import org.apache.log4j.Logger;
import com.tse.jira.scmactivity.model.ScmMessageBean;

/**
 * @author scmenthusiast@gmail.com
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
            
            ScmActivityDB.getInstance().inValidateDataSource();
            ScmMessageBean bean = ScmActivityDB.getInstance().getConnectionTest();            
            status = bean.getMessage();
            index = bean.getId();                   
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
    
    public void setDb_driver(String db_driver) {
        configBean.setDb_driver(db_driver);
    }
    
    public String getDb_driver() {
        return configBean.getDb_driver();
    }
    
    public void setMax_active(String max_active) {
        configBean.setMax_active(max_active);
    }
    
    public String getMax_active() {
        return configBean.getMax_active();
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
