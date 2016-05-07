/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.entities;

import net.java.ao.OneToMany;
import net.java.ao.OneToOne;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

/**
 *
 * @author vprasad
 */

@Table("scm_activity")
@Preload
public interface ScmActivity extends ScmEntity {              
    
    String getIssueKey();
    
    void setIssueKey(String issueKey);
    
    String getChangeId();
    
    void setChangeId(String changeId);
    
    String getChangeType();
    
    void setChangeType(String changeType);
    
    String getChangeAuthor();
    
    void setChangeAuthor(String changeAuthor);
    
    String getChangeDate();
    
    void setChangeDate(String changeDate);

    String getChangeLink();
    
    void setChangeLink(String changeLink);
    
    String getChangeBranch();
    
    void setChangeBranch(String changeBranch);
    
    String getChangeTag();
    
    void setChangeTag(String changeTag);
    
    String getChangeStatus();
    
    void setChangeStatus(String changeStatus);
    
    @OneToOne
    ScmMessage getChangeMessage();
    
    @OneToMany
    ScmFile[] getChangeFiles();
    
    @OneToMany
    ScmJob[] getScmJobs();
}
