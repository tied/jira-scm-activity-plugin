package com.tseg.jira.scmactivity.config;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.tseg.jira.scmactivity.model.ScmActivityOptionBean;

/**
 * @author vprasad
 */
public class ScmActivityOptionMgrImpl implements ScmActivityOptionMgr {
    
    private final ApplicationProperties applicationProperties;
    
    public ScmActivityOptionMgrImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public ScmActivityOptionBean getScmOptionProperties() {
        ScmActivityOptionBean configBean = new ScmActivityOptionBean();
        configBean.setJira_event_id(applicationProperties.getString(ScmActivityOptionMgr.JIRA_EVENT_ID));
        configBean.setExpand_count(applicationProperties.getString(ScmActivityOptionMgr.EXPAND_COUNT));
        return configBean;
    }

    @Override
    public void setScmOptionProperties(ScmActivityOptionBean configBean) {
        applicationProperties.setString(ScmActivityOptionMgr.JIRA_EVENT_ID, configBean.getJira_event_id());
        applicationProperties.setString(ScmActivityOptionMgr.EXPAND_COUNT, configBean.getExpand_count());
    }
    
}
