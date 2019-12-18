package com.xiao.framework.demo;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lix wang
 */
public class TaxiDispatcher {
    private static class Taxi {
        private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return this.location;
        }

        public void setLocation(Point location) {
            boolean reachedDestination;
            synchronized (this) {
                this.location = location;
                reachedDestination = location.equals(destination);
            }
            if (reachedDestination) {
                dispatcher.notifyAvailable(this);
            }
        }

    }

    private static class Dispatcher {
        private final Set<Taxi> taxis;
        private final Set<Taxi> availableTaxis;

        public Dispatcher(Set<Taxi> taxis, Set<Taxi> availableTaxis) {
            this.taxis = taxis;
            this.availableTaxis = availableTaxis;
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        public Image getImage() {
            Set<Taxi> copy;
            synchronized (this) {
                copy = new HashSet<>(taxis);
            }
            Image image = new Image();
            for (Taxi taxi : copy) {
                image.drawMarker(taxi.getLocation());
            }
            return image;
        }
    }

    private static class Point {
    }

    private static class Image {
        public void drawMarker(Point location) {}
    }
}
