/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.entities;

import java.sql.Types;
import net.java.ao.schema.SQLType;
import net.java.ao.schema.Table;

/**
 *
 * @author vprasad
 */

@Table("scm_message")
public interface ScmMessage extends ScmEntity {
    
    ScmActivity getScmActivity();
    
    void setScmActivity(ScmActivity scmActivity);
    
    @SQLType(Types.CLOB)
    String getMessage();
    
    @SQLType(Types.CLOB)
    void setMessage(String message);
}
