package com.victor.sexytalk.sexytalk.BackendlessClasses;

import com.backendless.BackendlessUser;

/**
 * Created by Victor on 28/02/2015.
 */
public class KissesCount {
    private String objectId;
    private String senderEmail;
    private String receiverEmail;
    private BackendlessUser sender;
    private BackendlessUser receiver;
    private int numberOfKisses;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }

    public String getSenderEmail() {return senderEmail;}
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail;}

    public String getReceiverEmail() {return receiverEmail;}
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail;}

    public BackendlessUser getSender(){return sender;}
    public void setSender(BackendlessUser sender) {this.sender = sender;}

    public BackendlessUser getReceiver(){return receiver;}
    public void setReceiver(BackendlessUser receiver) { this.receiver = receiver;}

    public int getNumberOfKisses(){return numberOfKisses;}
    public void setNumberOfKisses(int numberOfKisses) { this.numberOfKisses = numberOfKisses; }
}
