package com.jeremy.jmultithread.threadcoreknowledge.threadobjectclasscommonmethods;

import java.util.LinkedList;

public class ProducerConsumerModel {

    public static void main(String[] args) {
        StorageHouse storageHouse = new StorageHouse();
        Producer producer = new Producer(storageHouse);
        Consumer consumer = new Consumer(storageHouse);
        new Thread(producer).start();
        new Thread(consumer).start();
    }

    private static class Producer implements Runnable {
        private final StorageHouse mStorageHouse;

        public Producer(StorageHouse mStorageHouse) {
            this.mStorageHouse = mStorageHouse;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Product product = new Product("product" + mStorageHouse.size(),
                            mStorageHouse.size());
                    mStorageHouse.put(product);
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

    private static class Consumer implements Runnable {
        private final StorageHouse mStorageHouse;

        public Consumer(StorageHouse mStorageHouse) {
            this.mStorageHouse = mStorageHouse;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Product product = mStorageHouse.take();
                    System.out.println("Consume：" + product.toString());
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }
    }


    private static class StorageHouse {
        private static final int MAX_NUM = 10;
        private final LinkedList<Product> mProducts = new LinkedList();

        public synchronized void put(Product product) throws InterruptedException {
            if (mProducts.size() == MAX_NUM) {
                wait();
            }
            mProducts.add(product);
            System.out.println("Produce：" + product.toString());
            notify();
        }

        public synchronized Product take() throws InterruptedException {
            if (mProducts.size() == 0) {
                wait();
            }
            Product product = mProducts.pollFirst();
            notify();
            return product;
        }

        public int size() {
            return mProducts.size();
        }
    }

    private static class Product {
        private final String name;
        private final int price;

        public Product(String name, int price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
