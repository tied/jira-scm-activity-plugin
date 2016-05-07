package com.tseg.jira.scmactivity.config;

import com.tseg.jira.scmactivity.model.ScmActivityOptionBean;

/**
 * @author vprasad
 */
public abstract interface ScmActivityOptionMgr {
    public static final String JIRA_EVENT_ID = "com.tseg.jira.scmactivity.plugin.jira_event_id";
    public static final String EXPAND_COUNT = "com.tseg.jira.scmactivity.plugin.expand_count";
    public abstract ScmActivityOptionBean getScmOptionProperties();
    public abstract void setScmOptionProperties(ScmActivityOptionBean configBean);
}
