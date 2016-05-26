package com.tseg.jira.scmactivity.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vprasad
 */
@JsonAutoDetect
public class ScmMessageBean {
    
    @JsonProperty
    private long id;
    @JsonProperty
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long result) {
        this.id = result;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
        
}
