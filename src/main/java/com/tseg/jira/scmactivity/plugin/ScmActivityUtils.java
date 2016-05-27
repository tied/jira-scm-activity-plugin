package com.tseg.jira.scmactivity.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.renderer.wiki.WikiRendererFactory;
//import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.renderer.RenderContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 * @author vprasad
 */
public class ScmActivityUtils {
    
    private static final Logger LOGGER = Logger.getLogger(ScmActivityUtils.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static ScmActivityUtils scmActivityUtils = null;
    private ScmActivityUtils() {
    }
    
    static {    
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("utc"));
    }
    
    public static ScmActivityUtils getInstance() {
        if( scmActivityUtils == null ) {
            scmActivityUtils = new ScmActivityUtils();
        }
        return scmActivityUtils;
    }
    
    public Date getDateFromString(String date) {        
        Date formatedDate = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
            formatedDate = formatter.parse(date);
        } catch (java.text.ParseException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return formatedDate;
    }
    
    public Date getUtcDateFromString(String dateStr) {
        // example:    2011-05-26 10:54:41+xx:xx   (timezone is ignored because we parse utc timestamp date with utc parser)
        try {        
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error(e);
        }
        return null;
    }
    
    public static String getDateString(Date datetime) {
        return DATE_FORMAT.format(datetime); // example: 2011-05-26 10:54:41
    }
    
    public String getJiraAuthor(String changeAuthor) {
        User user = ComponentAccessor.getUserManager().getUser(changeAuthor);
        if( user != null ) {
            return user.getDisplayName();
        }
        return "";
    }
    
    public String getJiraAuthor2(String changeAuthor) {
        User user = ComponentAccessor.getUserManager().getUser(changeAuthor);
        if( user != null ) {
            return user.getDisplayName();
        }
        return changeAuthor;
    }
    
    public User getJiraAuthor4Git(String changeAuthorEmail) {    
        User user = null;
        for(User iUser : ComponentAccessor.getUserManager().getUsers()) {
            if(changeAuthorEmail.equalsIgnoreCase(iUser.getEmailAddress())) {                            
                user = iUser;
                break;
            }
        }        
        return user;
    }
    
    public String getWikiText(String plainText) {
        String wikiText = null;
        WikiRendererFactory wikiFactory = new WikiRendererFactory();
        wikiText = wikiFactory.getWikiRenderer().convertWikiToXHtml(new RenderContext(), plainText);                
        return wikiText;
    }        
    
}
