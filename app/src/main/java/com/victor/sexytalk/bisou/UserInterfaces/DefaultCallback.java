package com.victor.sexytalk.bisou.UserInterfaces;

import android.app.ProgressDialog;
import android.content.Context;

import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;

public class DefaultCallback<T> extends BackendlessCallback<T>
{
  Context context;
  ProgressDialog progressDialog;

  public DefaultCallback(Context context)
  {
    this.context = context;
    progressDialog = ProgressDialog.show(context, "", "Loading...", true);
  }

  public DefaultCallback(Context context, String loadingMessage)
  {
    this.context = context;
    progressDialog = ProgressDialog.show(context, "", loadingMessage, true);
  }

  @Override
  public void handleResponse( T response )
  {
    progressDialog.cancel();
  }

  @Override
  public void handleFault( BackendlessFault backendlessFault )
  {
    progressDialog.cancel();

  }


}
                                            