package com.tseg.jira.scmactivity.jql;

import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.jql.util.JqlDateSupport;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.tseg.jira.scmactivity.dao.impl.ScmActivityServiceImpl;

/**
 * @author vprasad
 */
public class ScmActivitySearch extends AbstractJqlFunction {
  
    private final IssueManager issueManager;
    private final JqlDateSupport jqlDateSupport;
    
    public ScmActivitySearch(IssueManager issueManager, JqlDateSupport jqlDateSupport) {
        this.issueManager = issueManager;
        this.jqlDateSupport = jqlDateSupport;
    }
    
    @Override
    public MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause tc) {
        MessageSet messageSet = validateNumberOfArgs(operand, 7);
        if ( messageSet.hasAnyErrors() ) {
            messageSet.addErrorMessage("The arguments should be in the form (\"Duration\", \"Change Type\", "
                    + "\"Change Author\", \"Change Branch\", \"Change Tag\", \"Change Status\", \"Change Search Text\")");
        }
        else {
            final List<String> args = operand.getArgs();
            String duration = args.get(0).trim();
            if ( duration != null && !duration.isEmpty() ) {
                if (!jqlDateSupport.validate(duration)) {
                    messageSet.addErrorMessage("The arguments should be in the form (\"Duration\", \"Change Type\", "
                    + "\"Change Author\", \"Change Branch\", \"Change Tag\", \"Change Status\", \"Change Search Text\")");
                    messageSet.addErrorMessage("The duration arg can be either a date or an expression like -5d or -8w.");
                }
            }
        }
        return messageSet;
    }

    @Override
    public List<QueryLiteral> getValues(QueryCreationContext qcc, FunctionOperand operand, TerminalClause tc) {
        final List<QueryLiteral> literals = new ArrayList<QueryLiteral>();
        
        //args
        final List<String> args = operand.getArgs();
        
        //user param
        String author = args.get(2).trim();
        //duration param
        String duration = args.get(0).trim();
        //type param
        String changeType = args.get(1).trim();
        //branch param
        String changeBranch = args.get(3).trim();
        //tag param
        String changeTag = args.get(4).trim();
        //status param
        String changeStatus = args.get(5).trim();
        //text param
        String changeText = args.get(6);
        
        //search changes
        String jqlDate = null;
        if (duration != null && !duration.isEmpty()) {            
            Date startDate = jqlDateSupport.convertToDate(duration);
            jqlDate = jqlDateSupport.getDateString(startDate);
        }
        
        List<String> issueKeys = ScmActivityServiceImpl.getInstance()
                .getScmActivitiesSearch(jqlDate, changeType, author, changeBranch, changeTag, changeStatus, changeText);
        
        if( issueKeys != null ) {
            for( String key : issueKeys ) {
                Issue issue = null;
                if( (issue = issueManager.getIssueObject(key)) != null ) {
                    literals.add(new QueryLiteral(operand, issue.getId()));
                }
            }
        }
        
        return literals;
    }

    @Override
    public int getMinimumNumberOfExpectedArguments() {
        return 7;
    }

    @Override
    public JiraDataType getDataType() {
        return JiraDataTypes.ISSUE;
    }

}
