package fr.pingtimeout.tyrion.model;

public class LockAccess implements Comparable<LockAccess> {


    private final Access access;
    private final Target target;


    public LockAccess(long enterTime, long exitTime, Target target) {
        this.access = new Access(enterTime, exitTime);
        this.target = target;
    }


    @Override
    public int compareTo(LockAccess that) {
        return this.access.compareTo(that.access);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LockAccess that = (LockAccess) o;

        if (!access.equals(that.access)) return false;
        if (!target.equals(that.target)) return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result = access.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }


    public Access getAccess() {
        return access;
    }

    public Target getTarget() {
        return target;
    }
}
