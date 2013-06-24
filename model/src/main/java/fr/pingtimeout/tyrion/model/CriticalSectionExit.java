package fr.pingtimeout.tyrion.model;

public class CriticalSectionExit extends CriticalSectionEvent {
    public CriticalSectionExit(Thread accessor, Object objectUnderLock) {
        super(accessor, objectUnderLock);
    }


    @Override
    String getType() {
        return "exit";
    }
}
