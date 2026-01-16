package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintTwoThreads {
    private static final Lock lock = new ReentrantLock();
    private static Condition turnChanged = lock.newCondition();
    private static boolean isEvenTurn = true;

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i <= 100; i += 2) {
                lock.lock();
                try {
                    while (!isEvenTurn) {
                        turnChanged.await();
                    }
                    System.out.println("Even: " + i);
                    isEvenTurn = false;
                    turnChanged.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 1; i <= 100; i += 2) {
                lock.lock();
                try {
                    while (isEvenTurn) {
                        turnChanged.await();
                    }
                    System.out.println("Odd: " + i);
                    isEvenTurn = true;
                    turnChanged.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        t1.start();
        t2.start();
    }
}
