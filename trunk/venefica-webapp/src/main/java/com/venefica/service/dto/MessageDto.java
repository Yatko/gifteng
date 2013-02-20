package com.venefica.service.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.venefica.model.Message;


/**
 * Message data transfer object.
 * 
 * @author Sviatoslav Grebenchukov
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageDto extends DtoBase {
	// in, out
	private Long id;
	// in, out
	private String text;
	// out
	private boolean owner;
	// out, in
	private String toName;
	// out
	private String toFullName;
	// out
	private String toAvatarUrl;
	// out
	private String fromName;
	// out
	private String fromFullName;
	// out
	private String fromAvatarUrl;
	// out
	private boolean read;
	// out
	private Date createdAt;

	// required for JAX-WS
	public MessageDto() {
	}

	public MessageDto(String text) {
		this.text = text;
	}
	
	public MessageDto(String toName, String text) {
		this.toName = toName;
		this.text = text;
	}

	public void update(Message message) {
		message.setText(text);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	
	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}
	
	public String getToFullName() {
		return toFullName;
	}

	public void setToFullName(String toFullName) {
		this.toFullName = toFullName;
	}

	public String getToAvatarUrl() {
		return toAvatarUrl;
	}

	public void setToAvatarUrl(String toAvatarUrl) {
		this.toAvatarUrl = toAvatarUrl;
	}
		
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	
	public String getFromFullName() {
		return fromFullName;
	}

	public void setFromFullName(String fromFullName) {
		this.fromFullName = fromFullName;
	}

	public String getFromAvatarUrl() {
		return fromAvatarUrl;
	}

	public void setFromAvatarUrl(String fromAvatarUrl) {
		this.fromAvatarUrl = fromAvatarUrl;
	}

	public boolean hasRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}