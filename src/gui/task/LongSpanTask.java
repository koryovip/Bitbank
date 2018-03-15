package gui.task;

import java.awt.EventQueue;

public abstract class LongSpanTask {

    protected abstract void handle();

    final public void execute() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handle();
                    }
                }).start();
            }
        });
    }
}
