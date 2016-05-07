package com.tseg.jira.scmactivity.config;

import com.tseg.jira.scmactivity.model.ScmActivityConfigBean;
import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * @author vprasad
 */
public class ScmActivityConfigMgrImpl implements ScmActivityConfigMgr {
    
    private final ApplicationProperties applicationProperties;
    
    public ScmActivityConfigMgrImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public ScmActivityConfigBean getScmConfigProperties() {
        ScmActivityConfigBean configBean = new ScmActivityConfigBean();
        configBean.setDb_user(applicationProperties.getString(ScmActivityConfigMgr.DB_USER));
        configBean.setDb_pass(applicationProperties.getString(ScmActivityConfigMgr.DB_PASS));
        configBean.setDb_url(applicationProperties.getString(ScmActivityConfigMgr.DB_URL));
        return configBean;
    }

    @Override
    public void setScmConfigProperties(ScmActivityConfigBean configBean) {
        applicationProperties.setString(ScmActivityConfigMgr.DB_USER, configBean.getDb_user());
        applicationProperties.setString(ScmActivityConfigMgr.DB_PASS, configBean.getDb_pass());
        applicationProperties.setString(ScmActivityConfigMgr.DB_URL, configBean.getDb_url());
    }
    
}
