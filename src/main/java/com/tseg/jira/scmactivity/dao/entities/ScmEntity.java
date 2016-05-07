/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tseg.jira.scmactivity.dao.entities;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

/**
 *
 * @author vprasad
 */
public interface ScmEntity extends RawEntity<Long> {
    @AutoIncrement
    @NotNull
    @PrimaryKey("ID")
    public long getID();
}
