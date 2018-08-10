
package com.smaaash.tv.background;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will execute the YahooInteractor out of the UI thread.
 *
 * Use this class to execute an YahooInteractor.
 *
 * This is just a sample implementation of how a YahooInteractor/Executor environment can be
 * implemented.
 * Ideally interactors should not know about Executor or MainThread dependency. Interactors client
 * code should get a Executor instance to execute interactors.
 *
 * @author Saghayam nadar
 */
public interface Executor {

  void run(final Interactor interactor);
}
