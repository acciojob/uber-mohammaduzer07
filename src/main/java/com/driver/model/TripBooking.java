package com.driver.model;

import javax.persistence.*;

@Entity
@Table(name = "TripBooking")
public class TripBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripBookingId;
    private String fromLocation;
    private String toLocation;
    private int distanceInKm;
    private TripStatus status;
    private int bill;
    @JoinColumn
    @ManyToOne
    Customer customer;

    @JoinColumn
    @ManyToOne
    Driver driver;

    public TripBooking(String fromLocation, String toLocation, int distanceInKm, TripStatus status, int bill, Customer customer, Driver driver) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.distanceInKm = distanceInKm;
        this.status = status;
        this.bill = bill;
        this.driver = driver;
        this.customer = customer;
    }
    public TripBooking(){

    }
    public int getTripBookingId() {
        return tripBookingId;
    }
    public void setTripBookingId(int tripBookingId) {
        this.tripBookingId = tripBookingId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public int getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(int distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus tripStatus) {
        this.status = status;
    }

    public int getBill() {
        return bill;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}