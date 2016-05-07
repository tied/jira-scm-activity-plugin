/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.entities;

import net.java.ao.schema.Table;

/**
 *
 * @author vprasad
 */

@Table("scm_files")
public interface ScmFile extends ScmEntity {
    
    ScmActivity getScmActivity();
    
    void setScmActivity(ScmActivity scmActivity);
    
    String getFileName();
    
    void setFileName(String fileName);
    
    String getFileAction();
    
    void setFileAction(String fileAction);
    
    String getFileVersion();
    
    void setFileVersion(String fileVersion);
}
