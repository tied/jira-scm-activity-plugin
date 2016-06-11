package com.tse.jira.scmactivity.config;

import com.tse.jira.scmactivity.model.ScmActivityOptionBean;

/**
 * @author scmenthusiast@gmail.com
 */
public abstract interface ScmActivityOptionMgr {
    public static final String JIRA_EVENT_ID = "com.tse.jira.scmactivity.plugin.jira_event_id";
    public static final String EXPAND_COUNT = "com.tse.jira.scmactivity.plugin.expand_count";
    public abstract ScmActivityOptionBean getScmOptionProperties();
    public abstract void setScmOptionProperties(ScmActivityOptionBean configBean);
}
