package com.tse.jira.scmactivity.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vprasad
 */
@JsonAutoDetect
public class ScmChangeSetBean {
    
    @JsonProperty
    private String issueKey;
    @JsonProperty
    private String changeType;
    @JsonProperty
    private String changeId;
    @JsonProperty
    private String changeAuthor;
    @JsonProperty
    private String changeDate;
    @JsonProperty
    private String changeMessage;
    @JsonProperty
    private List<ScmFileBean> changeFiles;
    @JsonProperty
    private String changeLink;
    @JsonProperty
    private String changeBranch;
    @JsonProperty
    private String changeTag;
    @JsonProperty
    private String changeStatus;
    @JsonProperty
    private boolean notifyEmail;
    @JsonProperty
    private String notifyAs;
    @JsonProperty
    private boolean changeUpdate;

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
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

    public String getChangeAuthor() {
        return changeAuthor;
    }

    public void setChangeAuthor(String changeAuthor) {
        this.changeAuthor = changeAuthor;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeMessage() {
        return changeMessage;
    }

    public void setChangeMessage(String changeMessage) {
        this.changeMessage = changeMessage;
    }

    public List<ScmFileBean> getChangeFiles() {
        return changeFiles;
    }

    public void setChangeFiles(List<ScmFileBean> changeFiles) {
        this.changeFiles = changeFiles;
    }    

    public String getChangeLink() {
        return changeLink;
    }

    public void setChangeLink(String changeLink) {
        this.changeLink = changeLink;
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

    public boolean getChangeUpdate() {
        return changeUpdate;
    }

    public void setChangeUpdate(boolean changeUpdate) {
        this.changeUpdate = changeUpdate;
    }    

    public String getChangeBranch() {
        return changeBranch;
    }

    public void setChangeBranch(String changeBranch) {
        this.changeBranch = changeBranch;
    }

    public String getChangeTag() {
        return changeTag;
    }

    public void setChangeTag(String changeTag) {
        this.changeTag = changeTag;
    }   
    
    public String getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(String changeStatus) {
        this.changeStatus = changeStatus;
    }
}
