package fr.pingtimeout.tyrion.model;

public class CriticalSectionEntered extends CriticalSectionEvent {
    public CriticalSectionEntered(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    @Override
    String getType() {
        return "enter";
    }
}
