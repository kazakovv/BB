package com.victor.sexytalk.sexytalk;

import android.net.Uri;

import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 10/12/2014.
 */
public class Messages {

    private BackendlessUser sender;
    private List<BackendlessUser> recepients;
    private String messageType;
    private String loveMessage;
    private Uri mMediaUri;

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

    public Uri getmMediaUri() {
        return mMediaUri;
    }
    public void setmMediaUri(Uri mMediaUri) {
        this.mMediaUri = mMediaUri;
    }


}
