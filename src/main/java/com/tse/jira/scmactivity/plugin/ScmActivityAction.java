package com.tse.jira.scmactivity.plugin;

//import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.user.ApplicationUser;
import com.tse.jira.scmactivity.model.ScmFileBean;
import com.tse.jira.scmactivity.model.ScmJobBean;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author scmenthusiast@gmail.com
 */
public class ScmActivityAction extends AbstractIssueAction {
    
    private long scmId;
    private String changeId;
    private String changeAuthor;
    private String changeType;
    private Date changeDate;
    private String scmMessage;
    private List<ScmFileBean> changeFiles;
    private String changeLink;
    private String changeBranch;
    private String changeTag;    
    private String changeStatus;
    private boolean headerVis;    
    private List<ScmJobBean> scmJobs;
    
    
    public ScmActivityAction(IssueTabPanelModuleDescriptor descriptor) {
        super(descriptor);
    }

    public long getScmId() {
        return scmId;
    }

    public void setScmId(long scmId) {
        this.scmId = scmId;
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

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getScmMessage() {
        return scmMessage;
    }

    public void setScmMessage(String scmMessage) {
        this.scmMessage = scmMessage;
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
    
    public boolean isHeaderVis() {
        return headerVis;
    }

    public void setHeaderVis(boolean headerVis) {
        this.headerVis = headerVis;
    }

    public List<ScmJobBean> getScmJobs() {
        return scmJobs;
    }

    public void setScmJobs(List<ScmJobBean> scmJobs) {
        this.scmJobs = scmJobs;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    
    @Override
    public Date getTimePerformed() {
        return this.changeDate;
    }

    @Override
    protected void populateVelocityParams(Map map) {
        map.put("scmId", scmId);
        map.put("changeId", changeId);
        map.put("changeAuthor", changeAuthor);
        map.put("changeType", changeType);
        map.put("jiraAuthor", ScmActivityUtils.getInstance().getJiraAuthor(changeAuthor));
        
        if( changeType.contains("git") ) {
            ApplicationUser user = ScmActivityUtils.getInstance()
                    .getJiraAuthorByEmail(changeAuthor);
            if( user != null ) {
                map.put("changeAuthor", user.getName());
                map.put("jiraAuthor", user.getDisplayName());
            }            
        }
        
        map.put("changeDate", changeDate);
        map.put("scmMessage", scmMessage);
        map.put("changeFiles", changeFiles);
        map.put("changeLink", changeLink);
        map.put("changeBranch", changeBranch);
        map.put("changeTag", changeTag);
        map.put("changeStatus", changeStatus);
        map.put("headerVis", headerVis);
        map.put("scmJobs", scmJobs);
    }
        
}
