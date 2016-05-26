package com.tseg.jira.scmactivity.config;

import com.tseg.jira.scmactivity.model.ScmActivityConfigBean;

/**
 * @author vprasad
 */
public abstract interface ScmActivityConfigMgr {
    public static final String DB_DRIVER = "com.tseg.jira.scmactivity.plugin.db_driver";
    public static final String DB_USER = "com.tseg.jira.scmactivity.plugin.db_user";
    public static final String DB_PASS = "com.tseg.jira.scmactivity.plugin.db_pass";
    public static final String DB_URL = "com.tseg.jira.scmactivity.plugin.db_url";
    public abstract ScmActivityConfigBean getScmConfigProperties();
    public abstract void setScmConfigProperties(ScmActivityConfigBean configBean);
}
