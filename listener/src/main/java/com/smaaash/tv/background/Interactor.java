
package com.smaaash.tv.background;


import com.smaaash.tv.apis.base.Error;

/**
 * Common callback to every YahooInteractor declared in the application. This callback represents a
 * execution unit for different use cases.
 *
 * By convention every interactor implementation will return the result using a Callback. That
 * callback should be executed over the UI thread.
 *
 * This is a simple YahooInteractor implementation. Other approach to do this could be use a class
 * instead of an callback and create a base YahooInteractor class that for every execution will use a
 * Request object and a callback implementation.
 *
 * @author saghayam nadar
 */
public interface Interactor<T> {
  void run();
  void onSuccess(T object);
  void onError(Error object);
}
