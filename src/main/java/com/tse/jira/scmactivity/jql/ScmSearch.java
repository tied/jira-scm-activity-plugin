package com.tse.jira.scmactivity.jql;

//import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import java.util.ArrayList;
import java.util.List;
import com.tse.jira.scmactivity.dao.impl.ScmActivityServiceImpl;
import org.apache.log4j.Logger;

/**
 * @author vprasad
 */
public class ScmSearch extends AbstractJqlFunction {
 
    private static final Logger LOGGER = Logger.getLogger(ScmSearch.class);
    private final IssueManager issueManager;
    
    public ScmSearch(IssueManager issueManager) {
        this.issueManager = issueManager;
    }
    
    @Override
    public MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause tc) {
        MessageSet messageSet = validateNumberOfArgs(operand, 1);
        if ( messageSet.hasAnyErrors() ) {
            messageSet.addErrorMessage("scmSearch (\"The Change Search Text\")");
        }        
        return messageSet;
    }

    @Override
    public List<QueryLiteral> getValues(QueryCreationContext qcc, FunctionOperand operand, TerminalClause tc) {
        final List<QueryLiteral> literals = new ArrayList<QueryLiteral>();
        
        //get args
        final List<String> args = operand.getArgs();
        String changeText = args.get(0).trim();
        
        List<String> issueKeys = ScmActivityServiceImpl.getInstance().getScmActivitiesSearch(changeText);
        
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
        return 1;
    }

    @Override
    public JiraDataType getDataType() {
        return JiraDataTypes.ISSUE;
    }

}
