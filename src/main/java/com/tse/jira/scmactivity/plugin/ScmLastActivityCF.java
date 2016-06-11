package com.tse.jira.scmactivity.plugin;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.tse.jira.scmactivity.dao.impl.ScmActivityServiceImpl;
import com.tse.jira.scmactivity.model.ScmActivityCustomFieldBean;

/**
 * @author scmenthusiast@gmail.com
 */
public class ScmLastActivityCF extends CalculatedCFType {  

    @Override
    public String getStringFromSingularObject(Object customFieldObject) {
        return customFieldObject.toString();
    }

    @Override
    public Object getSingularObjectFromString(String customFieldObject) throws FieldValidationException {
        return customFieldObject;
    }

    @Override
    public ScmActivityCustomFieldBean getValueFromIssue(CustomField field, Issue issue) 
    {        
        return ScmActivityServiceImpl.getInstance()
                .getScmActivity(issue.getKey(), "DESC",1);
    }
    
}
