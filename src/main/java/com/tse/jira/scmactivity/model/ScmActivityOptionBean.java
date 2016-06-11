package com.tse.jira.scmactivity.model;

/**
 * @author vprasad
 */
public class ScmActivityOptionBean {
    private String jira_event_id;
    private String expand_count;    

    public String getJira_event_id() {
        return jira_event_id;
    }

    public void setJira_event_id(String jira_event_id) {
        this.jira_event_id = jira_event_id;
    }

    public String getExpand_count() {
        return expand_count;
    }

    public void setExpand_count(String expand_count) {
        this.expand_count = expand_count;
    }
    
}
