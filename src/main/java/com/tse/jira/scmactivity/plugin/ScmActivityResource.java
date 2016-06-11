package com.tse.jira.scmactivity.plugin;

//import com.atlassian.crowd.embedded.api.User;
import com.tse.jira.scmactivity.model.ScmActivityNotifyBean;
import com.tse.jira.scmactivity.model.ScmChangeSetBean;
import com.tse.jira.scmactivity.model.ScmJobLinkBean;
import com.tse.jira.scmactivity.model.ScmMessageBean;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.IssueEventBundle;
import com.atlassian.jira.event.issue.IssueEventBundleFactory;
import com.atlassian.jira.event.issue.IssueEventManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.sal.api.user.UserManager;
import com.tse.jira.scmactivity.dao.ScmActivityDB;
import com.tse.jira.scmactivity.dao.ScmActivityService;
import com.tse.jira.scmactivity.dao.impl.ScmActivityServiceImpl;
import com.tse.jira.scmactivity.dao.impl.ScmFileServiceImpl;
import com.tse.jira.scmactivity.dao.impl.ScmJobServiceImpl;
import com.tse.jira.scmactivity.dao.impl.ScmMessageServiceImpl;
import com.tse.jira.scmactivity.model.ScmFileBean;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
@Path("/changeset")
public class ScmActivityResource {
    
    private static final Logger LOGGER = Logger.getLogger(ScmActivityResource.class);
    
    private UserManager userManager = null;
    private IssueManager issueManager = null;
    private PermissionManager permissionManager = null;
    private IssueEventManager issueEventManager = null;
    private IssueEventBundleFactory issueEventBundleFactory = null;
    private UserUtil userUtil = null;
    private String[] scm_repos = null;
    
    /**
     * Constructor to import components
     * @param userManager
     * @param issueManager
     * @param userUtil
     * @param permissionManager 
     * @param issueEventManager 
     * @param issueEventBundleFactory 
     */
    public ScmActivityResource(UserManager userManager, IssueManager issueManager, UserUtil userUtil, 
            PermissionManager permissionManager, IssueEventManager issueEventManager,
            IssueEventBundleFactory issueEventBundleFactory) {
    
        this.userManager = userManager;
        this.issueManager = issueManager;
        this.permissionManager = permissionManager;
        this.userUtil = userUtil;
        this.issueEventManager = issueEventManager;
        this.issueEventBundleFactory = issueEventBundleFactory;
        
        scm_repos = new String[] {  // supported scm systems list
            "subversion", "svn",
            "perforce", "p4",
            "gerrit",
            "git",
            "cvs",
            "clearcase", "cc", "ucm",
            "designsync", "sync",
            "ic", "ic-manage",
            "accurev",
            "collaborator", "coco",            
            "electric-cloud", "ec",            
            "jenkins",
            "swarm",
            "scm"
        };
    }
    
    
   /*
    * GET METHODS 
    */
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/{issueKey}{p:/?}{item:(([a-z]+)?)}")
    public Response getScmActivityByIssueKey(@PathParam("issueKey") String issueKey,
            @PathParam("item") String item) {
        
        LOGGER.debug("processing rest get request /activity/"+ issueKey + "/"+ item);
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(issueKey.trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.BROWSE_PROJECTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        //check required paramaters
        if( issueKey == null || "".equals(issueKey) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        ScmActivityService scmActivityService = ScmActivityServiceImpl.getInstance();
        
        if( !item.isEmpty() ) {
            if ( "last".equals(item) ) {
                return Response.ok(scmActivityService.getScmActivity(issueKey, "DESC",0)).build();
            } else {
                return Response.ok(scmActivityService.getScmActivity(issueKey, "ASC",0)).build();
            }
        } else {
            return Response.ok(scmActivityService.getScmActivities(issueKey)).build();
        }
    }
            
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/{changeType}/{issueKey}/{changeId}{p:/?}{notify:(([a-z]+)?)}")
    public Response getScmActivityByIssueKeyChangeId( @PathParam("changeType") String changeType, 
            @PathParam("issueKey") String issueKey, @PathParam("changeId") String changeId,
            @PathParam("notify") String notify ) {
    
        LOGGER.debug("processing rest get request /activity/"+ changeType + "/" + issueKey + "/"+ changeId);
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check permissions
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(issueKey.trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.BROWSE_PROJECTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        //check required paramaters
        if( issueKey == null || "".equals(issueKey) || changeId == null || "".equals(changeId) 
                || changeType == null || "".equals(changeType) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(changeType.toLowerCase().startsWith(repo)) index++;
        }
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+changeType+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        ScmActivityService scmActivityService = ScmActivityServiceImpl.getInstance();
        
        if( "notify".equals(notify) ) {
            ScmActivityNotifyBean scmActivity = scmActivityService.getScmActivityToNotify(issueKey, changeId, changeType);
            if(scmActivity != null) {                
                long customEventId = ScmActivityDB.customEventId;
                LOGGER.debug("Custom Notification Event ID ["+ customEventId+"]");
                if( customEventId > 0 ) {
                    fireCustomNotifyEvent(scmActivity, customEventId, null);
                }
            }
            return Response.ok("DONE").build();
        } else {            
            return Response.ok(scmActivityService.getScmActivity(issueKey, changeId, changeType)).build();
        }
    }
    
    
    
    
    
    
    
    
    /*
    * POST METHODS 
    */   
    
    
    
    /**
     * Rest method to add or update SCM change set item.
     * @param activityBean
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity")
    public Response addScmActivity(ScmChangeSetBean activityBean) {
        
        LOGGER.debug("processing rest post change request /activity/"+ activityBean.getChangeId()
            + "/" + activityBean.getIssueKey() + "/" + activityBean.getChangeType());
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( activityBean.getChangeId()==null || "".equals(activityBean.getChangeId()) 
                || activityBean.getChangeAuthor()==null || "".equals(activityBean.getChangeAuthor()) 
                || activityBean.getChangeDate()==null || "".equals(activityBean.getChangeDate())  
                || activityBean.getIssueKey()==null || "".equals(activityBean.getIssueKey()) 
                || activityBean.getChangeType()==null || "".equals(activityBean.getChangeType()) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check date format validity
        if( !activityBean.getChangeDate().trim().matches("(\\d+)-(\\d+)-(\\d+)\\s+(\\d+):(\\d+):(\\d+)") ) {
            messageBean.setMessage("[Error] Invalid date format ["+activityBean.getChangeDate()+"]. It should be in the format e.g. yyyy-MM-dd HH:mm:ss");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //verify files array        
        if( activityBean.getChangeFiles() !=null && !activityBean.getChangeFiles().isEmpty() ) {
            //int errorCheck = 0;
            for(ScmFileBean fileBean : activityBean.getChangeFiles()) {
                if( fileBean.getFileName()==null || "".equals(fileBean.getFileName())
                    || fileBean.getFileAction()==null || "".equals(fileBean.getFileAction()) ) {
                    messageBean.setMessage("[Error] Required fields are missing in change files array.");
                    return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
                }
            }
            /*if(errorCheck==1){
                messageBean.setMessage("[Error] Required fields are missing in change files array.");
                return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
            }*/
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(activityBean.getChangeType().toLowerCase().startsWith(repo)) index++;
        }
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+activityBean.getChangeType()+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(activityBean.getIssueKey().trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access[add comments] is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmActivityService scmActivityService = ScmActivityServiceImpl.getInstance();
        
        ScmMessageBean responseBean = scmActivityService.setScmActivity(activityBean);
        
        if ( responseBean.getId() != 0 ) {
            if ( activityBean.getNotifyEmail() == true ) {
                long customEventId = ScmActivityDB.customEventId;
                LOGGER.debug("[debug] processing notification > customEventId - "+ customEventId);
                if( customEventId != 0 ) {
                    fireCustomNotifyEvent(scmActivityService.getScmActivityToNotify(activityBean.getIssueKey(), activityBean.getChangeId(), 
                                    activityBean.getChangeType()), customEventId, activityBean.getNotifyAs());
                }
            }
            return Response.status(Status.CREATED).entity(responseBean).build();
        } else {
            return Response.status(Status.BAD_REQUEST).entity(responseBean).build();
        }
    }
    
    /**
     * Rest method to add or update SCM change message.
     * @version 2.0
     * @param activityBean
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/message")
    public Response addScmActivityChangeMessage(ScmChangeSetBean activityBean) {
        
        LOGGER.debug("processing rest post change message request /activity/message/"+ activityBean.getChangeId()
            + "/" + activityBean.getIssueKey() + "/" + activityBean.getChangeType());
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( activityBean.getChangeId()==null || "".equals(activityBean.getChangeId()) 
                || activityBean.getIssueKey()==null || "".equals(activityBean.getIssueKey()) 
                || activityBean.getChangeType()==null || "".equals(activityBean.getChangeType()) 
                || activityBean.getChangeMessage()==null || "".equals(activityBean.getChangeMessage()) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(activityBean.getChangeType().toLowerCase().startsWith(repo)) index++;
        }
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+activityBean.getChangeType()+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(activityBean.getIssueKey().trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.EDIT_ALL_COMMENTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access[edit comments] is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
                
        ScmMessageBean responseBean = ScmMessageServiceImpl.getInstance()
                .setScmMessage(activityBean);
        
        if ( responseBean.getId() != 0 ) {
            return Response.status(Status.CREATED).entity(responseBean).build();            
        } else {
            return Response.status(Status.BAD_REQUEST).entity(responseBean).build();
        }
    }
    
    
    /**
     * Rest method to add or update SCM change affected files.
     * @version 2.0
     * @param activityBean
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/files")
    public Response addScmActivityChangeFiles(ScmChangeSetBean activityBean) {
        
        LOGGER.debug("processing rest post change files request /activity/files/"+ activityBean.getChangeId()
            + "/" + activityBean.getIssueKey() + "/" + activityBean.getChangeType());
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( activityBean.getChangeId()==null || "".equals(activityBean.getChangeId()) 
                || activityBean.getIssueKey()==null || "".equals(activityBean.getIssueKey()) 
                || activityBean.getChangeType()==null || "".equals(activityBean.getChangeType()) 
                || activityBean.getChangeFiles()==null || activityBean.getChangeFiles().isEmpty() ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
       
        //verify files array
        //int errorCheck = 0;
        for(ScmFileBean fileBean : activityBean.getChangeFiles()) {
            if( fileBean.getFileName()==null || "".equals(fileBean.getFileName())
                || fileBean.getFileAction()==null || "".equals(fileBean.getFileAction()) ) {
                messageBean.setMessage("[Error] Required fields are missing in change files array.");
                return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
            }
        }
        /*if(errorCheck==1){
            messageBean.setMessage("[Error] Required fields are missing in change files array.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }*/
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {            
            if(activityBean.getChangeType().toLowerCase().startsWith(repo)) index++;
        }        
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+activityBean.getChangeType()+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(activityBean.getIssueKey().trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.EDIT_ALL_COMMENTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access[edit comments] is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmMessageBean responseBean = ScmFileServiceImpl.getInstance()
                .setScmFiles(activityBean);
        
        if ( responseBean.getId() != 0 ) {
            return Response.status(Status.CREATED).entity(responseBean).build();            
        } else {
            return Response.status(Status.BAD_REQUEST).entity(responseBean).build();
        }
    }
    
    
    /**
     * Rest method to add SCM Job item.
     * @param activityBean
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/joblink")
    public Response addScmJobLink(ScmJobLinkBean activityBean) {
    
        LOGGER.debug("processing rest post job link request /joblink/"+ activityBean.getChangeId()
            + "/" + activityBean.getIssueKey() + "/" + activityBean.getChangeType());
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( activityBean.getJobName()==null || "".equals(activityBean.getJobName()) 
                || activityBean.getIssueKey()==null || "".equals(activityBean.getIssueKey()) 
                || activityBean.getChangeId()==null || "".equals(activityBean.getChangeId()) 
                || activityBean.getChangeType()==null || "".equals(activityBean.getChangeType()) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(activityBean.getChangeType().toLowerCase().startsWith(repo)) index++;
        }
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+activityBean.getChangeType()+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(activityBean.getIssueKey().trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.ADD_COMMENTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project access[add comments] is required.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmMessageBean responseBean = ScmJobServiceImpl.getInstance()
                .setScmJob(activityBean);
        
        if ( responseBean.getId() != 0 ) {
            if ( activityBean.getNotifyEmail() == true ) {
                long customEventId = ScmActivityDB.customEventId;
                if( customEventId != 0 ) {
                    fireCustomNotifyEvent(ScmActivityServiceImpl.getInstance()
                            .getScmActivityToNotify(activityBean.getIssueKey(), activityBean.getChangeId(), 
                                    activityBean.getChangeType()), customEventId, activityBean.getNotifyAs());
                }
            }
            return Response.status(Status.CREATED).entity(responseBean).build();        
        } else {        
            return Response.status(Status.BAD_REQUEST).entity(responseBean).build();
        }
    }
    
    
    
    
     
   /*
    * DELETE METHODS 
    */
    
    
    
    /**
     * Rest method to remove SCM Activity by change type + issue key + change id
     * @version 2.0
     * @param changeType
     * @param issueKey
     * @param changeId
     * @return 
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/unset/by/{changeType}/{issueKey}/{changeId}")
    public Response removeScmActivity(@PathParam("changeType") String changeType, @PathParam("issueKey") String issueKey, 
            @PathParam("changeId") String changeId) {
        
        LOGGER.debug("processing rest delete change request /activity/"+changeType+"/"+ issueKey + "/"+ changeId);
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( issueKey == null || issueKey.equals("") || changeId == null || "".equals(changeId) 
                || changeType == null || "".equals(changeType) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }

        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(changeType.toLowerCase().startsWith(repo)) index++;            
        }
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+changeType+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(issueKey.trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, 
                issue, userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project administrators only.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmActivityServiceImpl.getInstance().deleteScmActivity(issueKey, changeId, changeType);
        
        return Response.status(Status.NO_CONTENT).build();
    }            
    
    
    /**
     * Rest method to remove SCM Job Link
     * @param changeType
     * @param issueKey
     * @param changeId
     * @param jobId
     * @return 
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/joblink/unset/by/{changeType}/{issueKey}/{changeId}/{jobId}")
    public Response removeScmJobLink(@PathParam("changeType") String changeType, @PathParam("issueKey") String issueKey, @PathParam("changeId") String changeId,
            @PathParam("jobId") Long jobId) {
        
        LOGGER.debug("processing rest get request /joblink/"+ changeType + "/" + issueKey + "/"+ changeId);
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( issueKey == null || "".equals(issueKey) || changeId == null || "".equals(changeId)
                || jobId == 0 || changeType == null || "".equals(changeType) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {
            if(changeType.toLowerCase().startsWith(repo)) index++;
        }        
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+changeType+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(issueKey.trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project administrators only.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmJobServiceImpl.getInstance()
                .deleteScmJob(issueKey, changeId, changeType, jobId);
        
        return Response.status(Status.NO_CONTENT).build();
    }
    
    /**
     * Rest method to remove SCM activity affected file
     * @version 2.0
     * @param changeType
     * @param issueKey
     * @param changeId
     * @param fileId
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/activity/file/unset/by/{changeType}/{issueKey}/{changeId}/{fileId}")
    public Response removeScmJobLinks(@PathParam("changeType") String changeType, @PathParam("issueKey") String issueKey, 
            @PathParam("changeId") String changeId, @PathParam("fileId") long fileId) {
        
        LOGGER.debug("processing rest get request /joblinks/"+ changeType + "/" + issueKey + "/"+ changeId);
        
        //message object
        ScmMessageBean messageBean = new ScmMessageBean();
        
        //check required paramaters
        if( issueKey == null || "".equals(issueKey) || changeId == null || "".equals(changeId)
                || changeType == null || "".equals(changeType) ) {
            messageBean.setMessage("[Error] Required fields are missing.");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        //check change type
        int index = 0;
        for(String repo : scm_repos) {            
            if(changeType.toLowerCase().startsWith(repo)) index++;
        }        
        if(index==0) {
            messageBean.setMessage("[Error] Change type ["+changeType+"] is not supported. Define valid change type i.e. "
                    + "SCM Change Type_Instance/Repo name e.g. git_engsw");
            return Response.status(Status.BAD_REQUEST).entity(messageBean).build();
        }
                       
        //check permissions 
        String username = userManager.getRemoteUser().getUsername();
        MutableIssue issue = issueManager.getIssueObject(issueKey.trim().toUpperCase());
        if( permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, issue, 
                userUtil.getUserByKey(username)) == false ) {
            messageBean.setMessage("[Error] Permission denied. Project administrators only.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        ScmFileServiceImpl.getInstance()
                .deleteScmFile(issueKey, changeId, changeType, fileId);
                
        return Response.status(Status.NO_CONTENT).build();
    }
    
    
    
    
    /**
     * OTHERS
     */
    
    
    /**
     * method to send custom email event
     * @version 2.0
     * @param activityBean
     * @param customEventId
     * @param notifyAs
     */
    public void fireCustomNotifyEvent(ScmActivityNotifyBean activityBean, 
            long customEventId, String notifyAs) {    
        
        if ( ComponentAccessor.getEventTypeManager().isEventTypeExists(customEventId) ) { //send email
            if( activityBean !=null  ) {
            MutableIssue issue = issueManager.getIssueObject(activityBean.getIssueKey());

            ApplicationUser checkedInUser = null;
            
            if( notifyAs != null && !"".equals(notifyAs)) {
                checkedInUser = userUtil.getUserByKey(notifyAs);
            } else {
                if( activityBean.getChangeType().contains("git") ) {
                    checkedInUser = ScmActivityUtils.getInstance().getJiraAuthorByEmail(activityBean.getChangeAuthor());
                    activityBean.setJiraAuthor(checkedInUser.getDisplayName());
                } else {                    
                    checkedInUser = userUtil.getUserByKey(activityBean.getChangeAuthor());
                }
            }
        
            activityBean.setBaseUrl(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
            
            if( checkedInUser == null ) { //if null send email using remote user
                LOGGER.debug("change user("+activityBean.getChangeAuthor()+")/notifyas user("+notifyAs+") is not exists! "
                        + "so setting remote executing user.");
                
                checkedInUser = userUtil.getUserByKey(userManager.getRemoteUser().getUsername());
            }
        
            Map<String, Object> context = new HashMap<String, Object>(); // params object
            context.put("scmactivity", activityBean);
        
            IssueEvent myEvent = new IssueEvent(issue, context, checkedInUser, customEventId, true);
            IssueEventBundle eventBundle = issueEventBundleFactory.wrapInBundle(myEvent);
            issueEventManager.dispatchEvent(eventBundle);

            //issueEventManager.dispatchEvent(customEventId, issue, context, checkedInUser, true); //fire email event
            LOGGER.debug("Fired an event ["+customEventId+"] for scm id - "+ activityBean.getId());
            }
        } else {
            LOGGER.debug("Custom Notification Event ID ["+customEventId+"] not exists.");
        }
    }
        
}
