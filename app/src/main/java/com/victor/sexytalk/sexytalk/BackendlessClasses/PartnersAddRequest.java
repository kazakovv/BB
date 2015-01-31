package com.victor.sexytalk.sexytalk.BackendlessClasses;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

/**
 * Tablica s chakashti odobrene partner requests
 */
public class PartnersAddRequest {
    private String objectId;
    private BackendlessUser userRequesting;
    private BackendlessUser partnerToConfirm;
    private String email_userRequesting;
    private String email_partnerToConfirm;
    private String username_userRequesting;
    private String username_userToConfirm;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }

    public BackendlessUser getUserRequesting() {return userRequesting;}

    public void setUserRequesting(BackendlessUser userRequesting) {
        this.userRequesting = userRequesting;
    }

    public BackendlessUser getPartnerToConfirm() {return partnerToConfirm;}

    public void setPartnerToConfirm(BackendlessUser partnerToConfirm) {
        this.partnerToConfirm = partnerToConfirm;
    }

    public String getEmail_userRequesting() {return email_userRequesting;}

    public void setEmail_userRequesting(String email_userRequesting) {
        this.email_userRequesting = email_userRequesting;
    }

    public String getEmail_partnerToConfirm() {return email_partnerToConfirm;}

    public void setEmail_partnerToConfirm(String email_partnerToConfirm) {
        this.email_partnerToConfirm = email_partnerToConfirm;
    }

    public String getUsername_userRequesting() {return username_userRequesting;}
    public void setUsername_userRequesting(String username_userRequesting) {
        this.username_userRequesting = username_userRequesting;
    }
    public String getUsername_userToConfirm() {return username_userToConfirm;}
    public void setUsername_userToConfirm(String username_userToConfirm) {
        this.username_userToConfirm = username_userToConfirm;
    }
}
