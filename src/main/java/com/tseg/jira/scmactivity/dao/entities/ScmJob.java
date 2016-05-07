/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.entities;

import net.java.ao.schema.Table;

/**
 *
 * @author vprasad
 */

@Table("scm_job")
public interface ScmJob extends ScmEntity {
    
    ScmActivity getScmActivity();
    
    void setScmActivity(ScmActivity scmActivity);
    
    String getJobName();
    
    void setJobName(String jobName);
    
    String getJobStatus();
    
    void setJobStatus(String jobStatus);
    
    String getJobLink();
    
    void setJobLink(String jobLink);
}
