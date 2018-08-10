
package com.smaaash.tv.background;

import android.os.Handler;
import android.os.Looper;


/**
 * MainThread implementation based on a Handler instantiated over the main looper obtained from
 * Looper class.
 *
 * @author saghayam nadar
 */
public class MainThreadImpl implements MainThread {

  private Handler handler;

  public MainThreadImpl() {
    this.handler = new Handler(Looper.getMainLooper());
  }

  public void post(Runnable runnable) {
    handler.post(runnable);
  }
}
