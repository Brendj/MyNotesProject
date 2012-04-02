/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

public class KsPasswordCallback implements CallbackHandler
{
   private Map<String, String> passwords = new HashMap<String, String>();

   public KsPasswordCallback()
   {
      passwords.put("939d6862-52b9-490e-9bac-d88cee5ccb3f", "1");
   }

   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
   {
      for (int i = 0; i < callbacks.length; i++)
      {
         WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
         String pass = passwords.get(pc.getIdentifier());
         if (pass != null)
         {
            pc.setPassword(pass);
            return;
         }
      }
   }

   public void setAliasPassword(String alias, String password)
   {
      passwords.put(alias, password);
   }
}