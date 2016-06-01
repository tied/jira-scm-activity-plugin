package com.tseg.jira.scmactivity.plugin;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.tseg.jira.scmactivity.dao.impl.ScmActivityServiceImpl;
import com.tseg.jira.scmactivity.model.ScmActivityCustomFieldBean;

/**
 * @author vprasad
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
        ScmActivityCustomFieldBean scmBean = ScmActivityServiceImpl.getInstance()
                .getScmActivity(issue.getKey(), "DESC",1);
        
        scmBean.setChangeDate(ScmActivityUtils.getDateFromString(scmBean.getChangeDate()).toString());
        
        return scmBean;
    }
    
}
