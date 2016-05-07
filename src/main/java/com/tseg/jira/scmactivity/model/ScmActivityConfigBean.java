package com.tseg.jira.scmactivity.model;

/**
 * @author vprasad
 */
public class ScmActivityConfigBean {    
    private String db_user;
    private String db_pass;
    private String db_driver;
    private String db_url;
    //private String jira_event_id;
    //private String expand_count;

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    public String getDb_pass() {
        return db_pass;
    }

    public void setDb_pass(String db_pass) {
        this.db_pass = db_pass;
    }

    public String getDb_driver() {
        return db_driver;
    }

    public void setDb_driver(String db_driver) {
        this.db_driver = db_driver;
    }    

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    /*public String getJira_event_id() {
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
    }*/
    
}
