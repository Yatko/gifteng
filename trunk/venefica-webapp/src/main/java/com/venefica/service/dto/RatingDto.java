/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venefica.service.dto;

import com.venefica.model.Rating;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author gyuszi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RatingDto extends DtoBase {
    
    // in, out
    @NotNull
    private Long adId;
    // in, out
    private String text;
    // in, out
    @NotNull
    private int value;
    // in
    private Long toUserId;
    // out
    private Date ratedAt;
    // out
    private UserDto fromUser;
    // out
    private UserDto toUser;
    
    public RatingDto() {
    }
    
    public RatingDto(Long adId, Long toUserId, int value) {
        this.adId = adId;
        this.toUserId = toUserId;
        this.value = value;
    }
    
    public RatingDto(Long adId, Long toUserId, String text, int value) {
        this.adId = adId;
        this.toUserId = toUserId;
        this.text = text;
        this.value = value;
    }
    
    /**
     * Constructs the DTO object form the domain object.
     * 
     * @param rating domain object
     */
    public RatingDto(Rating rating) {
        value = rating.getValue();
        text = rating.getText();
        ratedAt = rating.getRatedAt();
        adId = rating.getAd().getId();
        fromUser = new UserDto(rating.getFrom());
        toUser = new UserDto(rating.getTo());
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Date getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(Date ratedAt) {
        this.ratedAt = ratedAt;
    }

    public UserDto getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserDto fromUser) {
        this.fromUser = fromUser;
    }

    public UserDto getToUser() {
        return toUser;
    }

    public void setToUser(UserDto toUser) {
        this.toUser = toUser;
    }
    
}
