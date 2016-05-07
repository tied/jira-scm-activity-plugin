package com.tseg.jira.scmactivity.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.impl.ScmActivityServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author vprasad
 */
public class ScmActivityTabPanel extends AbstractIssueTabPanel {

    private static final Logger LOGGER = Logger.getLogger(ScmActivityTabPanel.class);
    
    @Override
    public List getActions(Issue issue, User user) {
        
        List<ScmActivityAction> activities = new ArrayList<ScmActivityAction>();
        
        ScmActivity[] scmActivities = ScmActivityServiceImpl.getInstance().getScmActivities(issue.getKey());
        
        ScmActivityAction actionBean = null;
        long i = 0;
        
        for( ScmActivity scmActivity : scmActivities) {
            
            actionBean = new ScmActivityAction(this.descriptor);
            actionBean.setScmId(scmActivity.getID());
            actionBean.setChangeId(scmActivity.getChangeId());
            actionBean.setChangeType(scmActivity.getChangeType());
            actionBean.setChangeAuthor(scmActivity.getChangeAuthor());
            Date changeDate = ScmActivityUtils.getInstance().getUtcDateFromString(scmActivity.getChangeDate());
            actionBean.setChangeDate(changeDate);
            actionBean.setChangeLink(scmActivity.getChangeLink());
            if(scmActivity.getChangeMessage() !=null) {
                actionBean.setScmMessage(ScmActivityUtils.getInstance()
                    .getWikiText(scmActivity.getChangeMessage().getMessage()));
            }
            actionBean.setChangeFiles(Arrays.asList(scmActivity.getChangeFiles()));
            actionBean.setChangeBranch(scmActivity.getChangeBranch());
            actionBean.setChangeTag(scmActivity.getChangeTag());
            actionBean.setChangeStatus(scmActivity.getChangeStatus());
            if ( i < ScmActivityEntityManager.expandCount ) {
                actionBean.setHeaderVis(true);
            }
            actionBean.setScmJobs(Arrays.asList(scmActivity.getScmJobs()));
            activities.add(actionBean);
            i++;
            
        }                
        
        if ( activities.isEmpty() ) {
          List<GenericMessageAction> emptySet = new ArrayList<GenericMessageAction>();
          emptySet.add(new GenericMessageAction("There are no activities yet on this issue."));
          return emptySet;
        }
        
        return activities;
    }

    @Override
    public boolean showPanel(Issue issue, User au) {
        return true;
    }
    
}
