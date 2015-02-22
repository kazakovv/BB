package com.victor.sexytalk.sexytalk.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;
import com.victor.sexytalk.sexytalk.UserInterfaces.DefaultCallback;
import com.victor.sexytalk.sexytalk.R;
import com.victor.sexytalk.sexytalk.Statics;


/**
 * Created by Victor on 19/10/2014.
 */
public class MaleOrFemaleDialog extends DialogFragment {

    BackendlessUser currentUser;
    Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        currentUser = Backendless.UserService.CurrentUser();


        context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_menu_title)
                .setItems(R.array.sex_options, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item

                  String messageCallback = context.getResources().getString(R.string.saving_preferences_message);

                   switch(which) {
                    case 0:
                        dialog.dismiss();
                        //mainMessage.setText(R.string.main_message_male);

                        //update v backendless che e male
                        currentUser.setProperty(Statics.KEY_MALE_OR_FEMALE, Statics.SEX_MALE);
                        Backendless.UserService.update(currentUser, new DefaultCallback<BackendlessUser>(context,messageCallback) {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                super.handleResponse(backendlessUser);
                                Toast.makeText(context,
                                        R.string.selection_saved_successfully, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                super.handleFault(backendlessFault);
                                String error = backendlessFault.getMessage();
                                Toast.makeText(context,
                                        R.string.selection_not_saved, Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    case 1:
                       // update v backendless che e female
                        dialog.dismiss();

                        currentUser.setProperty(Statics.KEY_MALE_OR_FEMALE, Statics.SEX_FEMALE);
                        Backendless.UserService.update(currentUser, new DefaultCallback<BackendlessUser>(context, messageCallback) {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                super.handleResponse(backendlessUser);
                                Toast.makeText(context,
                                        R.string.selection_saved_successfully,Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                super.handleFault(backendlessFault);
                                String error = backendlessFault.getMessage();
                                Toast.makeText(context,
                                        R.string.selection_not_saved,Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                }
            }
        });
        return builder.create();

    }
}
