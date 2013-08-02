package fr.pingtimeout.tyrion.model;

public class Access implements Comparable<Access> {
    private final long enterTime;
    private final long exitTime;

    public Access(long enterTime, long exitTime) {
        this.enterTime = enterTime;
        this.exitTime = exitTime;
    }

    @Override
    public int compareTo(Access that) {
        return (int) (this.enterTime - that.enterTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Access access = (Access) o;

        if (enterTime != access.enterTime) return false;
        if (exitTime != access.exitTime) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (enterTime ^ (enterTime >>> 32));
        result = 31 * result + (int) (exitTime ^ (exitTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "[" + enterTime + ", " + exitTime + ']';
    }
}
