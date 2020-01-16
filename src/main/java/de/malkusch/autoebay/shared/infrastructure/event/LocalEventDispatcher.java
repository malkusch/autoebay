package de.malkusch.autoebay.shared.infrastructure.event;

import java.util.HashMap;
import java.util.Map;

public final class LocalEventDispatcher implements EventDispatcher {

    private final Map<Class<? extends Event>, EventHandler<?>> handlers = new HashMap<>();

    @Override
    public <T extends Event> void registerHandler(Class<T> type, EventHandler<T> handler) {
        handlers.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dispatch(Event event) throws Exception {
        var handler = handlers.get(event.getClass());
        if (handlers == null) {
            return;
        }
        ((EventHandler<Event>) handler).handle((Event) event);
    }
}
