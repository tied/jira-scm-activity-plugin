/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao;

import com.tseg.jira.scmactivity.dao.entities.ScmActivity;
import com.tseg.jira.scmactivity.dao.entities.ScmFile;
import com.tseg.jira.scmactivity.model.ScmFileBean;
import com.tseg.jira.scmactivity.model.ScmMessageBean;
import java.util.List;

/**
 *
 * @author vprasad
 */
public interface ScmFileService {
    
    ScmMessageBean setScmFiles(List<ScmFileBean> changeFiles, ScmActivity scmActivity);
    
    ScmFile[] getScmFiles(long scmId);
    
    void deleteScmFile(ScmFile scmFile);
}
