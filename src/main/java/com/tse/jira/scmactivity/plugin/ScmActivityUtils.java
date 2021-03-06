package com.tse.jira.scmactivity.plugin;

//import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.renderer.wiki.WikiRendererFactory;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.renderer.RenderContext;
import com.tse.jira.scmactivity.dao.ScmActivityDB;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class ScmActivityUtils {
    
    private static final Logger LOGGER = Logger.getLogger(ScmActivityUtils.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Map<String, String> GIT_EMAIL_CACHE = new HashMap<String, String>();
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
    
    public static Date getDateFromString(String dateStr) {        
        // example: 2011-05-26 10:54:41+xx:xx (timezone is ignored because we parse utc timestamp date with utc parser)
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
        if( ScmActivityDB.is_map_users == true ) {
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(changeAuthor);
            if( user != null ) {
                return user.getDisplayName();
            }
        }
        return "";
    }
    
    public ApplicationUser getJiraAuthorByEmail(String email) {
        if( ScmActivityDB.is_map_users == true ) {
        String userKey = GIT_EMAIL_CACHE.get(email.toLowerCase());
            if( userKey != null && !userKey.isEmpty()) {
                ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(userKey);
                if( user != null && email.equalsIgnoreCase(user.getEmailAddress()) ) {
                    LOGGER.debug("User ["+userKey+"] picked from Git Cached Emails.");
                    return user;
                } else {
                    GIT_EMAIL_CACHE.remove(email.toLowerCase());
                }
            }

            for(ApplicationUser iUser : ComponentAccessor.getUserManager().getUsers()) {
                if(iUser.getEmailAddress().equalsIgnoreCase(email)) {
                    GIT_EMAIL_CACHE.putIfAbsent(email.toLowerCase(), iUser.getUsername());
                    return iUser;
                }
            }
        }
        return null;
        /*ApplicationUser user = null;
        for(ApplicationUser iUser : ComponentAccessor.getUserManager().getUsers()) {
            if(changeAuthorEmail.equalsIgnoreCase(iUser.getEmailAddress())) {                            
                user = iUser;
                break;
            }
        }        
        return user;*/
    }
    
    public String getWikiText(String plainText) {
        WikiRendererFactory wikiFactory = new WikiRendererFactory();
        return wikiFactory.getWikiRenderer()
                .convertWikiToXHtml(new RenderContext(), plainText);
    }
    
}
