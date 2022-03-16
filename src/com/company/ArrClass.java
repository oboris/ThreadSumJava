package com.company;

public class ArrClass implements Runnable {
    private final int dim;
    private final int threadNum;
    public final int[] arr;

    public ArrClass(int dim, int threadNum) {
        this.dim = dim;
        arr = new int[dim];
        this.threadNum = threadNum;
        for(int i = 0; i < dim; i++){
            arr[i] = i;
        }
        arr[dim/3] = -100;
    }

    public long partSum(int startIndex, int finishIndex){
        long sum = 0;
        for(int i = startIndex; i < finishIndex; i++){
            sum += arr[i];
        }
        return sum;
    }

    private long sum = 0;

    synchronized private long getSum() {
        while (getThreadCount()<threadNum){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sum;
    }

    synchronized public void collectSum(long sum){
        this.sum += sum;
    }

    private int threadCount = 0;
    synchronized public void incThreadCount(){
        threadCount++;
        notify();
    }

    private int getThreadCount() {
        return threadCount;
    }

    private Bound[] bounds;
    public long threadSum(){
        bounds = new Bound[threadNum];
        bounds[0] = new Bound(0, dim / 2);
        bounds[1] = new Bound(dim / 2, dim);
        Thread[] threadSums = new Thread[threadNum];
        threadSums[0] = new Thread(this);
        threadSums[0].setName("0");
        threadSums[1] = new Thread(this);
        threadSums[1].setName("1");

        threadSums[0].start();
        threadSums[1].start();

        return getSum();
    }

    @Override
    public void run() {
        int index = Integer.parseInt(Thread.currentThread().getName());
        long sum = partSum(bounds[index].startIndex(), bounds[index].finishIndex());
        collectSum(sum);
        incThreadCount();
    }
}
