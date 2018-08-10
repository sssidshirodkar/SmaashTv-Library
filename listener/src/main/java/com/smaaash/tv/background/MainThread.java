
package com.smaaash.tv.background;

/**
 * UI thread abstraction created to change the execution context from any thread to the UI thread.
 *
 * @author Pedro Vicente Gómez Sánchez
 */
public interface MainThread {

  void post(final Runnable runnable);
}
