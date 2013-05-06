/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venefica.service.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author gyuszi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestDto extends DtoBase {
    
    // out
    private Long id;
    // out
    private UserDto user;
    // out
    private Date requestedAt;

    public RequestDto() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }
    
}
