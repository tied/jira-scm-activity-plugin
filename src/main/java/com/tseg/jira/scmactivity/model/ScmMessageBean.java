package com.tseg.jira.scmactivity.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author vprasad
 */
@JsonAutoDetect
public class ScmMessageBean {
    
    @JsonProperty
    private long result;
    @JsonProperty
    private String message;

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
        
}
