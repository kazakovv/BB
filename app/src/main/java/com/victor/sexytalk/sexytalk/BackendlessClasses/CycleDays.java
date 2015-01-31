package com.victor.sexytalk.sexytalk.BackendlessClasses;

import com.backendless.BackendlessUser;

import java.util.Date;

/**
 * Created by Victor on 29/12/14.
 */
public class CycleDays {
    private String objectId;
    private Boolean sendCalendarUpdateToPartners;
    private BackendlessUser sender;
    private String senderEmail;
    private String satusText;
    private Date firstDayOfCycle;
    private int averageCycleLength;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }

    public Boolean getSendCalendarUpdateToPartners() {return sendCalendarUpdateToPartners;}

    public void setSendCalendarUpdateToPartners(Boolean sendCalendarUpdateToPartners) {
        this.sendCalendarUpdateToPartners = sendCalendarUpdateToPartners; }

    public BackendlessUser getSender() {return sender;}

    public void setSender(BackendlessUser sender) {this.sender = sender;}

    public String getSenderEmail() {return senderEmail;}

    public void setSenderEmail(String senderEmail) {this.senderEmail = senderEmail;}

    public String getSatusText() {return satusText;}

    public void setSatusText(String satusText) {this.satusText = satusText;}

    public Date getFirstDayOfCycle() {return firstDayOfCycle;}

    public void setFirstDayOfCycle(Date firstDayOfCycle) {this.firstDayOfCycle = firstDayOfCycle;}

    public int getAverageCycleLength() {return averageCycleLength;}

    public void setAverageCycleLength(int averageCycleLength) {this.averageCycleLength = averageCycleLength;}


}
