package com.tseg.jira.scmactivity.model;

/**
 * @author vprasad
 */
public class ScmActivityConfigBean {    
    private String db_user;
    private String db_pass;
    private String db_driver;
    private String db_url;
    private String max_active;

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

    public String getMax_active() {
        return max_active;
    }

    public void setMax_active(String max_active) {
        this.max_active = max_active;
    }    
    
}
