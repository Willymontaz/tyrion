package fr.pingtimeout.tyrion.model;

public abstract class CriticalSectionEvent {

    private final long timestamp;

    private final Accessor accessor;

    private final Target target;

    public CriticalSectionEvent(Thread accessingThread, Object objectUnderLock) {
        this.timestamp = System.currentTimeMillis();
        this.accessor = new Accessor(accessingThread);
        this.target = new Target(objectUnderLock);
    }


    public String toJson() {
        return ""
                + "{"
                + "\"type\": \"" + getType() + "\""
                + ", \"timestamp\": \"" + timestamp + "\""
                + ", \"accessor\": "
                /**/ + "{"
                /**/ + " \"id\" : \"" + accessor + "\""
                /**/ + ", \"name\" : \"" + accessor.name + "\""
                /**/ + " }"
                + ", \"target\": \"" + target + "\""
                + "}"
                ;
    }

    abstract String getType();


    class Accessor {
        private final long id;

        private final String name;

        Accessor(Thread accessingThread) {
            this.id = accessingThread.getId();
            this.name = accessingThread.getName();
        }

        @Override
        public String toString() {
            return Thread.class.getName() + "@" + id;
        }
    }


    class Target {

        private final Class<?> clazz;

        private final long hashcode;

        Target(Object target) {
            this.clazz = target.getClass();
            this.hashcode = System.identityHashCode(target);
        }

        @Override
        public String toString() {
            return clazz.toString() + "@" + hashcode;
        }
    }
}
