package com.tse.jira.scmactivity.config;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tse.jira.scmactivity.model.ScmActivityOptionBean;

/**
 * @author scmenthusiast@gmail.com
 */
public class ScmActivityOptionMgrImpl implements ScmActivityOptionMgr {
    
    private final ApplicationProperties applicationProperties;
    
    public ScmActivityOptionMgrImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public ScmActivityOptionBean getScmOptionProperties() {
        ScmActivityOptionBean configBean = new ScmActivityOptionBean();
        configBean.setIs_map_users(applicationProperties.getString(ScmActivityOptionMgr.IS_MAP_USERS));
        configBean.setJira_event_id(applicationProperties.getString(ScmActivityOptionMgr.JIRA_EVENT_ID));
        configBean.setExpand_count(applicationProperties.getString(ScmActivityOptionMgr.EXPAND_COUNT));
        return configBean;
    }

    @Override
    public void setScmOptionProperties(ScmActivityOptionBean configBean) {
        applicationProperties.setString(ScmActivityOptionMgr.IS_MAP_USERS, configBean.getIs_map_users());
        applicationProperties.setString(ScmActivityOptionMgr.JIRA_EVENT_ID, configBean.getJira_event_id());
        applicationProperties.setString(ScmActivityOptionMgr.EXPAND_COUNT, configBean.getExpand_count());
    }
    
}
