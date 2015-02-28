package com.victor.sexytalk.sexytalk.Helper;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.BackendlessClasses.KissesCount;

/**
 * Created by Victor on 28/02/2015.
 */
public class CreateKissTables {
    public static void createTables(BackendlessUser userA, BackendlessUser userB) {
         //tablica user A izprashta celuvki na user B
        KissesCount kissesCountUserA = new KissesCount();
        kissesCountUserA.setNumberOfKisses(0);
        kissesCountUserA.setSenderEmail(userA.getEmail());
        kissesCountUserA.setReceiverEmail(userB.getEmail());
        kissesCountUserA.setSender(userA);
        kissesCountUserA.setReceiver(userB);

        Backendless.Data.of(KissesCount.class).save(kissesCountUserA, new AsyncCallback<KissesCount>() {
            @Override
            public void handleResponse(KissesCount kissesCount) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });

    }
}
