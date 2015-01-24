package com.victor.sexytalk.sexytalk;

import com.backendless.BackendlessUser;

/**
 * 1 kam 1 kato partner add request
 */
public class PartnerDeleteRequest {
    private String objectId;
    private BackendlessUser userDeleting;
    private BackendlessUser userDeleted;
    private String email_userDeleting;
    private String email_userDeleted;
    private String username_userDeleting;
    private String username_userDeleted;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId( String objectId ) {
        this.objectId = objectId;
    }

    public BackendlessUser getUserDeleting() {return userDeleting;}

    public void setUserDeleting(BackendlessUser userDeleting) {
        this.userDeleting = userDeleting;
    }

    public BackendlessUser getUserDeleted() {return userDeleted;}

    public void setUserDeleted(BackendlessUser userDeleted) {
        this.userDeleted = userDeleted;
    }

    public String getEmail_userDeleting() {return email_userDeleting;}

    public void setEmail_userDeleting(String email_userDeleting) {
        this.email_userDeleting = email_userDeleting;
    }

    public String getEmail_userDeleted() {return email_userDeleted;}

    public void setEmail_userDeleted(String email_userDeleted) {
        this.email_userDeleted = email_userDeleted;
    }

    public String getUsername_userDeleting() {return username_userDeleting;}
    public void setUsername_userDeleting(String username_userDeleting) {
        this.username_userDeleting = username_userDeleting;
    }
    public String getUsername_userDeleted() {return username_userDeleted;}
    public void setUsername_userDeleted(String username_userDeleted) {
        this.username_userDeleted = username_userDeleted;
    }
}
