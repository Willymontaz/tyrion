package fr.pingtimeout.tyrion.model;

public class AccessorAccess implements Comparable<AccessorAccess> {
    private final Access access;
    private final Accessor accessor;

    public AccessorAccess(Access access, Accessor accessor) {
        this.access = access;
        this.accessor = accessor;
    }

    @Override
    public int compareTo(AccessorAccess that) {
        return this.access.compareTo(that.access);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessorAccess that = (AccessorAccess) o;

        if (!access.equals(that.access)) return false;
        if (!accessor.equals(that.accessor)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = access.hashCode();
        result = 31 * result + accessor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return access + " by " + accessor;
    }
}
