package de.malkusch.autoebay.shared.infrastructure.event;

public final class EventPublisher {

    private final EventDispatcher dispatcher;
    private static volatile EventPublisher INSTANCE;

    public EventPublisher(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        INSTANCE = this;
    }

    public static void publishEvent(Event event) {
        try {
            INSTANCE.dispatcher.dispatch(event);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
