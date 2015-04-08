package com.victor.sexytalk.bisou.BackendlessClasses;

import com.backendless.BackendlessUser;

import java.util.Date;
import java.util.List;

/**
 * Created by Victor on 10/12/2014.
 */
public class Messages {

    private BackendlessUser sender;
    private List<BackendlessUser> recepients;
    private String messageType;
    private String loveMessage;
    private String mediaUrl;
    private String backendlessFilePath;
    private String senderUsername;
    private String recepientEmails;
    private String senderEmail;
    private String objectId;
    private int kissNumber;
    private Date created;
    private Date updated;
    private Date opened;

    public BackendlessUser getSender() {
        return sender;
    }

    public void setSender(BackendlessUser sender) {
        this.sender = sender;
    }

    public List<BackendlessUser> getRecepients() {
        return recepients;
    }

    public void setRecepients(List<BackendlessUser>  recepients) {
        this.recepients = recepients;
    }



    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getLoveMessage() {
        return loveMessage;
    }
    public void setLoveMessage(String loveMessage) {
        this.loveMessage = loveMessage;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mMediaUrl) {
        this.mediaUrl = mMediaUrl;
    }

    public String getBackendlessFilePath() {
        return backendlessFilePath;
    }

    public void setBackendlessFilePath(String backendlessFilePath) {
        this.backendlessFilePath = backendlessFilePath;
    }

    public void setSederUsername(String senderUsername) {
    this.senderUsername = senderUsername;
    }

    public String getSenderUsername() { return senderUsername; }

    public void setRecepientEmails(String recepientEmails) {
        this.recepientEmails = recepientEmails;
    }

    public String getRecepientEmails() { return recepientEmails; }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderEmail() { return senderEmail; }


    public Date getCreated()  { return created;  }

    public void setCreated( Date created ) { this.created = created; }

    public Date getUpdated() { return updated; }

    public void setUpdated( Date updated ) { this.updated = updated; }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }

    public Date getOpened() { return opened; }

    public void setOpened( Date opened ) { this.opened = opened; }

    public int getKissNumber() {return kissNumber;}
    public void setKissNumber(int kissNumber) {this.kissNumber = kissNumber;}
}

