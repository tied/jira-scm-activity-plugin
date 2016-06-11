package com.tse.jira.scmactivity.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vprasad
 */

@JsonAutoDetect
public class ScmJobBean {
    
    @JsonProperty
    private long id;
    @JsonProperty
    private String issueKey;
    @JsonProperty
    private String changeId;
    @JsonProperty
    private String changeType;
    @JsonProperty
    private String jobName;
    @JsonProperty
    private String jobLink;
    @JsonProperty
    private String jobStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
        
    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }
    
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }    

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }        
    
}
