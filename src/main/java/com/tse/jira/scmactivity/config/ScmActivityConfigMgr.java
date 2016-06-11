package com.tse.jira.scmactivity.config;

import com.tse.jira.scmactivity.model.ScmActivityConfigBean;

/**
 * @author scmenthusiast@gmail.com
 */
public abstract interface ScmActivityConfigMgr {
    public static final String MAX_ACTIVE = "com.tse.jira.scmactivity.plugin.max_active";
    public static final String DB_DRIVER = "com.tse.jira.scmactivity.plugin.db_driver";
    public static final String DB_USER = "com.tse.jira.scmactivity.plugin.db_user";
    public static final String DB_PASS = "com.tse.jira.scmactivity.plugin.db_pass";
    public static final String DB_URL = "com.tse.jira.scmactivity.plugin.db_url";
    public abstract ScmActivityConfigBean getScmConfigProperties();
    public abstract void setScmConfigProperties(ScmActivityConfigBean configBean);
}
