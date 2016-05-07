/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.impl;

import java.sql.SQLException;
import net.java.ao.EntityManager;
import com.tseg.jira.scmactivity.dao.ScmActivityEntityManager;
import com.tseg.jira.scmactivity.dao.ScmFileService;
import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmFile;
import com.tseg.jira.scmactivity.model.ScmFileBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.util.List;
import net.java.ao.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author vprasad
 */
public class ScmFileServiceImpl implements ScmFileService {

    private static final Logger LOGGER = Logger.getLogger(ScmFileServiceImpl.class);
    private static ScmFileServiceImpl scmFileService = null;
        
    private ScmFileServiceImpl() { }

    public static ScmFileServiceImpl getInstance() {
        if ( scmFileService == null ) {
            scmFileService = new ScmFileServiceImpl();
        }
        return scmFileService;
    }
    
    @Override
    public ScmMessageBean setScmFiles(List<ScmFileBean> changeFiles, ScmActivity scmActivity) {
        ScmMessageBean messageBean = new ScmMessageBean();
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            if( scmActivity != null && scmActivity.getID() != 0 ) {
                
                //clean if existing
                ScmFile[] scmFiles = getScmFiles(scmActivity.getID());                
                for(ScmFile file : scmFiles) {
                    deleteScmFile(file);
                }
                
                //add new
                for(ScmFileBean fileBean : changeFiles) {
                    ScmFile scmFile = manager.create(ScmFile.class);
                    scmFile.setFileName(fileBean.getFileName());
                    scmFile.setFileAction(fileBean.getFileAction().toUpperCase());
                    scmFile.setFileVersion(fileBean.getFileVersion());
                    scmFile.setScmActivity(scmActivity);
                    scmFile.save();
                }
                
                messageBean.setResult(1);
                messageBean.setMessage("[Info] "+scmActivity.getIssueKey() +" > "+scmActivity.getChangeId()+""
                    + " activity files rows for scm id["+scmActivity.getID()+"] is updated.");
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
            messageBean.setMessage(ex.getLocalizedMessage());
        }
        return messageBean;
    }
    
    @Override
    public ScmFile[] getScmFiles(long scmId) {
        try {
            
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            
            return manager.find(ScmFile.class, Query.select()
                    .where("scmActivityID = ?", scmId));
        
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void deleteScmFile(ScmFile scmFile) {
        try {
            EntityManager manager = ScmActivityEntityManager.getInstance().getEntityManager();
            manager.delete(scmFile);
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    
}
