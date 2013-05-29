import java.util.Vector;

public class HelloWorld {
    private Object lock = new Object();

    public synchronized void synchronizedMethod() {
        System.out.println("Hello ");
    }

    public void synchronizedBlock() {
        synchronized (lock) {
            System.out.println("World");
        }
    }

    public void externalSynchronizedMethods() {
        Vector<String> stringVector = new Vector<String>();
        StringBuffer stringBuffer = new StringBuffer();
        stringVector.add("Foo");
        stringVector.add("Foo");
        for (String s : stringVector) {
            stringBuffer.append(s);
        }
        System.out.println(stringBuffer);
    }

    public static void main(String... args) {
        HelloWorld hello = new HelloWorld();

        hello.synchronizedMethod();
        hello.synchronizedBlock();
        hello.externalSynchronizedMethods();
    }


}
