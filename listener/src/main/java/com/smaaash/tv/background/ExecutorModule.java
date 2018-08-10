
package com.smaaash.tv.background;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 * @author Saghayam Nadar
 */

public final class ExecutorModule {

//  Executor provideExecutor(ThreadExecutor threadExecutor) {
//    return threadExecutor;
//  }

   MainThread provideMainThread(MainThreadImpl mainThread) {
    return mainThread;
  }

    private final static ExecutorModule module = new ExecutorModule();

    public static ExecutorModule provideExecutor(){
        return module;
    }

//    ThreadExecutor executor;
    MainThreadImpl mainThread;
    ExecutorService singleThread = Executors.newSingleThreadExecutor();

    private ExecutorModule(){

//        executor = new ThreadExecutor();
        mainThread = new MainThreadImpl();
    }

    public void runOnUiThread(Runnable runnable){
        mainThread.post(runnable);
    }

//    public void submitTask(YahooInteractor interactor){
//        executor.run(interactor);
//    }

    public void runOnBackground(Runnable runnable){
        singleThread.submit(runnable);
    }

    public void runOnBackground(final Interactor interactor){
        singleThread.submit(new Runnable() {
            @Override
            public void run() {
                interactor.run();
            }
        });
    }

}
