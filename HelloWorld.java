public class HelloWorld {
    private Object lock = new Object();

    public synchronized void synchronizedMethod() {
        System.out.println("Hello ");
    }

    public void synchronizedBlock() {
        synchronized(lock) {
            System.out.println("World");
        }
    }

    public static void main(String... args) {
        HelloWorld hello = new HelloWorld();
        
        hello.synchronizedMethod();
        hello.synchronizedBlock();
    }
}
