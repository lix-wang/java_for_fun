package com.xiao.framework.thread.demo;

import com.xiao.framework.thread.ThreadNotSafe;
import com.xiao.framework.thread.ThreadSafe;
import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Monitor of vehicle movement tracker。
 *
 * @author lix wang
 */
@ThreadSafe
public class MonitorVehicleTracker {
    private final Map<String, MutablePoint> locations;

    public MonitorVehicleTracker(@NotNull Map<String, MutablePoint> locations) {
        this.locations = new ConcurrentHashMap<>(locations);
    }

    @ThreadSafe
    static class DelegatingVehicleTracker {
        private final ConcurrentMap<String, Point> locations;
        private final Map<String, Point> unmodifiableMap;

        public DelegatingVehicleTracker(Map<String, Point> points) {
            this.locations = new ConcurrentHashMap<>(points);
            this.unmodifiableMap = Collections.unmodifiableMap(locations);
        }

        public Map<String, Point> getLocations() {
            return this.unmodifiableMap;
        }

        public Point getLocation(String id) {
            return this.locations.get(id);
        }

        public void setLocation(String id, int x, int y) {
            if (this.locations.replace(id, new Point(x, y)) == null) {
                throw new IllegalArgumentException("Invalid vehicle name: " + id);
            }
        }
    }

    public synchronized Map<String, MutablePoint> getLocations() {
        return deepCopy(this.locations);
    }

    public synchronized MutablePoint getLocation(String id) {
        MutablePoint mutablePoint = this.locations.get(id);
        return mutablePoint == null ? null : new MutablePoint(mutablePoint);
    }

    public synchronized void setLocation(String id, int x, int y) {
        MutablePoint mutablePoint = this.locations.get(id);
        if (mutablePoint == null) {
            throw new IllegalArgumentException("No such ID: " + id);
        }
        mutablePoint.x = x;
        mutablePoint.y = y;
    }

    private static Map<String, MutablePoint> deepCopy(@NotNull Map<String, MutablePoint> mutablePointMap) {
        Map<String, MutablePoint> result = new HashMap<>();
        mutablePointMap.entrySet().forEach(entry -> result.put(entry.getKey(), new MutablePoint(entry.getValue())));
        return Collections.unmodifiableMap(result);
    }

    @ThreadNotSafe
    private static class MutablePoint {
        public int x;
        public int y;

        public MutablePoint() {
            this.x = 0;
            this.y = 0;
        }

        public MutablePoint(MutablePoint mutablePoint) {
            this.x = mutablePoint.x;
            this.y = mutablePoint.y;
        }
    }

    @Immutable
    private static class Point {
        public final int x;
        public final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
