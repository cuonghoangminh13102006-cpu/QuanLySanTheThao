package com.sportbooking.modules.catalog.model;

/**
 * Model Sân thể thao
 * Tương ứng bảng SAN và catalog.yards trong anta-backend
 */
public class Court {
    public String id;
    public String name;
    public String facility;
    public String address;
    public String type;
    public double price;
    public String open;
    public String close;
    public String operational;
    public boolean visible;
    public String ownerId;
    public String ownerName;
    public String description;

    public Court(String id, String name, String facility, String address, String type, double price,
                 String open, String close, String operational, boolean visible,
                 String ownerId, String ownerName, String description) {
        this.id = id;
        this.name = name;
        this.facility = facility;
        this.address = address;
        this.type = type;
        this.price = price;
        this.open = open;
        this.close = close;
        this.operational = operational;
        this.visible = visible;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.description = description;
    }
}
