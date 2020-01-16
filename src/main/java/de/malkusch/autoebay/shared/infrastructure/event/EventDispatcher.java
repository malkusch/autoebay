package de.malkusch.autoebay.shared.infrastructure.event;

public interface EventDispatcher {

    void dispatch(Event event) throws Exception;

    public static interface EventHandler<T extends Event> {
        void handle(T event) throws Exception;
    }

    public <T extends Event> void registerHandler(Class<T> type, EventHandler<T> handler);

}
