// Synchronization
// This example demonstrates how to use synchronization in Java to ensure that only one thread can access a critical section of code at a time.
// 1. Synchronized Method
// 2. Synchronized Block
// 3. Synchronized Static Method
// 4. Synchronized Static Block

class Demo{
    synchronized void fun(){
        for(int i=0;i<5;i++){
            System.out.println(Thread.currentThread().getName()+" : "+i);
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                System.out.println(e);
            }
        }
    }
}

class MyThread extends Thread{
    Demo obj;
    MyThread(Demo obj){
        this.obj=obj;
    }
    public void run(){
        obj.fun();
    }
}

class Client{
    public static void main(String args[]){
        Demo obj=new Demo();
        MyThread t1=new MyThread(obj);
        MyThread t2=new MyThread(obj);
        t1.start();
        t2.start();
    }
}