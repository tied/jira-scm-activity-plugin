package com.tse.jira.scmactivity.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vprasad
 * @version 2.0
 */

@JsonAutoDetect
public class ScmJobLinkBean {
    
    @JsonProperty
    private long jobId;
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
    @JsonProperty
    private boolean notifyEmail;
    @JsonProperty
    private String notifyAs;
    @JsonProperty
    private boolean jobUpdate;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
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

    public void setJobUrl(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public boolean getNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(boolean notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public String getNotifyAs() {
        return notifyAs;
    }

    public void setNotifyAs(String notifyAs) {
        this.notifyAs = notifyAs;
    }

    public boolean getJobUpdate() {
        return jobUpdate;
    }

    public void setJobUpdate(boolean jobUpdate) {
        this.jobUpdate = jobUpdate;
    }        

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
}
