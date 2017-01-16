package com.tse.jira.scmactivity.config;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.tse.jira.scmactivity.dao.ScmActivityDB;
import org.apache.log4j.Logger;
import com.tse.jira.scmactivity.model.ScmActivityOptionBean;

/**
 * @author scmenthusiast@gmail.com
 */
public class ScmActivityOptionAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ScmActivityOptionAction.class);
    private final ScmActivityOptionMgr scmOptionMgr;
    private ScmActivityOptionBean optionBean = new ScmActivityOptionBean();
    private String submitted;
    private String status;
    
    public ScmActivityOptionAction(ScmActivityOptionMgr scmOptionMgr) {
        this.scmOptionMgr = scmOptionMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasPermission(0) ) {
            return "error";
        }
        if (this.submitted == null) {
            LOGGER.debug("Loading scm options.");
            optionBean = scmOptionMgr.getScmOptionProperties();
        } else {
            LOGGER.debug("Saving scm options.");
            scmOptionMgr.setScmOptionProperties(optionBean);
            
            LOGGER.debug("jira event id -> "+ optionBean.getJira_event_id());
            LOGGER.debug("jira expand count -> "+ optionBean.getExpand_count());
            LOGGER.debug("is map users -> "+ optionBean.getIs_map_users());
            try {
                ScmActivityDB.is_map_users = Boolean.parseBoolean(optionBean.getIs_map_users());
                ScmActivityDB.customEventId = Integer.parseInt(optionBean.getJira_event_id());
                ScmActivityDB.expandCount = Integer.parseInt(optionBean.getExpand_count());
                status="Saved!";
            } catch (NumberFormatException e){
                LOGGER.error(e);
                status = "Failed! "+e.getLocalizedMessage();
            }
        }
        return "success";
    }    

    public String getIs_map_users() {
        return optionBean.getIs_map_users();
    }
    
    public void setIs_map_users(String is_map_users) {
        if( "".equals(is_map_users) ) is_map_users = "false";
        optionBean.setIs_map_users(is_map_users);
    }
    
    public String getJira_event_id() {
        return optionBean.getJira_event_id();
    }

    public void setJira_event_id(String jira_event_id) {
        if( "".equals(jira_event_id) ) jira_event_id = "0";
        optionBean.setJira_event_id(jira_event_id);
    }
    
    public String getExpand_count() {
        return optionBean.getExpand_count();
    }

    public void setExpand_count(String expand_count) {
        optionBean.setExpand_count(expand_count);
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }
}
