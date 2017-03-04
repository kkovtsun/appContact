
package com.kovtsun.apple.WeatherGson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kovtsun.apple.WeatherGson.Location;

public class Example {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("current")
    @Expose
    private Current current;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

}
