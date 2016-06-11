package com.tse.jira.scmactivity.config;

import com.tse.jira.scmactivity.model.ScmActivityConfigBean;
import com.atlassian.jira.config.properties.ApplicationProperties;

/**
 * @author scmenthusiast@gmail.com
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
        configBean.setDb_driver(applicationProperties.getString(ScmActivityConfigMgr.DB_DRIVER));
        configBean.setMax_active(applicationProperties.getString(ScmActivityConfigMgr.MAX_ACTIVE));
        return configBean;
    }

    @Override
    public void setScmConfigProperties(ScmActivityConfigBean configBean) {
        applicationProperties.setString(ScmActivityConfigMgr.DB_USER, configBean.getDb_user());
        applicationProperties.setString(ScmActivityConfigMgr.DB_PASS, configBean.getDb_pass());
        applicationProperties.setString(ScmActivityConfigMgr.DB_URL, configBean.getDb_url());
        applicationProperties.setString(ScmActivityConfigMgr.DB_DRIVER, configBean.getDb_driver());
        applicationProperties.setString(ScmActivityConfigMgr.MAX_ACTIVE, configBean.getMax_active());
    }
    
}
