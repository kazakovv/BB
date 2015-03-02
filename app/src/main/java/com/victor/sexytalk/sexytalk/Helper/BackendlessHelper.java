package com.victor.sexytalk.sexytalk.Helper;

import android.view.MenuItem;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.victor.sexytalk.sexytalk.BackendlessClasses.KissesCount;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnerDeleteRequest;
import com.victor.sexytalk.sexytalk.BackendlessClasses.PartnersAddRequest;
import com.victor.sexytalk.sexytalk.Main;
import com.victor.sexytalk.sexytalk.Statics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor on 28/02/2015.
 */
public class BackendlessHelper {

    //sazdavane na redove s kiss entries, kogato 2 choveka stavat partniori
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

    //proverka za delete partner request

    public static void checkForDeletePartnerRequest(final BackendlessUser mCurrentUser) {
        String whereClause = "email_userDeleted='" + mCurrentUser.getEmail() + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        Backendless.Data.of(PartnerDeleteRequest.class).find(dataQuery, new AsyncCallback<BackendlessCollection<PartnerDeleteRequest>>() {
            @Override
            public void handleResponse(final BackendlessCollection<PartnerDeleteRequest> partnerDeleteRequest) {
                final List<PartnerDeleteRequest> pendingDeleteRequests = partnerDeleteRequest.getData();

                //svaliame masiv s tekushtite partniori
                BackendlessUser[] currentListWithPartners;
                if (mCurrentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                    currentListWithPartners = (BackendlessUser[]) mCurrentUser.getProperty(Statics.KEY_PARTNERS);
                } else {
                    currentListWithPartners = new BackendlessUser[0];
                }

                //kopirame currentlistWithPartners v array, za da moze po-lesno da triem ot nego
                List<BackendlessUser> currentListWithPartnersArray = new ArrayList<BackendlessUser>();
                for (BackendlessUser user : currentListWithPartners) {
                    currentListWithPartnersArray.add(user);
                }
                //iztrivame partniorite koito sa pratili delete request ot currentListWithPartnersArray
                for (PartnerDeleteRequest deleteRequest : pendingDeleteRequests) {
                    BackendlessUser userToRemove = deleteRequest.getUserDeleting();
                    //po emaila tarsim dali ima takav user v sastesvuvashtite partniori
                    //i go iztrivame, ako go namerim
                    for (int i = 0; i < currentListWithPartnersArray.size(); i++) {
                        String emailOfExisingPartner = currentListWithPartnersArray.get(i).getEmail();
                        if (userToRemove.getEmail().equals(emailOfExisingPartner)) {
                            //iztrivame toya partnior ot spisaka
                            currentListWithPartnersArray.remove(i);
                        }
                    }


                }
                //kopirame vsichki ostavashti partiori v novia spisak s partniori
                BackendlessUser[] newListWithPartners = new BackendlessUser[currentListWithPartnersArray.size()];
                int i = 0;
                for (BackendlessUser user : currentListWithPartnersArray) {
                    newListWithPartners[i] = user;
                    i++;
                }
                //updatevame novia spisak s partniori za tekushtia potrebitel
                mCurrentUser.setProperty(Statics.KEY_PARTNERS, newListWithPartners);
                //updatevame i na servera
                Backendless.UserService.update(mCurrentUser, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser backendlessUser) {
                        //iztrivame pending delete request
                        for (PartnerDeleteRequest deleteRequest : pendingDeleteRequests) {
                            Backendless.Data.of(PartnerDeleteRequest.class).remove(deleteRequest, new AsyncCallback<Long>() {
                                @Override
                                public void handleResponse(Long aLong) {
                                    /*
                                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    !!!TOVA E KRAIAT NA USPESHNOTO IZTRIVAME NA PARTNER!!!
                                    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                     */
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    //TODO: tr da se pomisli kakvo da se napravi v sluchai na greshka
                                }
                            });
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        //TODO: ne e zle da napravim neshto, ako ima greshka s updatevamento na partionri
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //TODO: NESHTO TR DA SE NAPRAVI
            }
        });
    }

//proverka dali niama chakashiti zaiavki
    public static void checkForPendingParnerRequests(BackendlessUser mCurrentUser, final MenuItem addPartner) {

        String whereClause = "email_partnerToConfirm='" + mCurrentUser.getEmail() + "'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause);
        Backendless.Data.of(PartnersAddRequest.class).find(query, new AsyncCallback<BackendlessCollection<PartnersAddRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<PartnersAddRequest> partners) {

                if (partners.getData().size() > 0) {
                    //ako query vrashta rezultat, znachi ima pending request
                    if(addPartner !=null) {
                        //pokazvame butona za dobaviane na partniori, ako reference kam nego ne e null
                        addPartner.setVisible(true);
                    }
                    Statics.pendingPartnerRequest = true;

                } else {
                    //ako ne varne nishto mahame butona
                    if(addPartner !=null) {
                        addPartner.setVisible(false);
                    }
                    Statics.pendingPartnerRequest = false;

                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
               if (addPartner != null) {
                    addPartner.setVisible(false);
                }
                Statics.pendingPartnerRequest = false;
            }
        });
    }
    /*
    CHECK DALI NIAMA NOVI PARTNIORI ZA DOBAVIANE
     */

    public static void checkAndUpdatePartners(final BackendlessUser mCurrentUser) {
       BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = "email='" +mCurrentUser.getEmail() +"'";
        dataQuery.setWhereClause(whereClause);
        Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> user) {
                if(user.getCurrentPage().size()>0) {
                    //bi triabvalo da ima samo 1 potrebitel tuk
                    BackendlessUser currentUser = user.getCurrentPage().get(0);
                    if(currentUser.getProperty(Statics.KEY_PARTNERS) instanceof BackendlessUser[]) {
                        BackendlessUser[] updatedPartnersList = (BackendlessUser[]) currentUser.getProperty(Statics.KEY_PARTNERS);
                        //updatevame lokalno
                        currentUser.setProperty(Statics.KEY_PARTNERS, updatedPartnersList);
                        Backendless.UserService.setCurrentUser(currentUser);
                    }

                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //niama kakvo da napravim
            }
        });

    }

}
