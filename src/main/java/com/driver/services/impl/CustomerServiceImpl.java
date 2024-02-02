package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> bookingList = customer.getTripBookingList();

		//Now we will set the cab as available for each and every trip booked by this customer,
		//who is going to be deleted
		for(TripBooking trip : bookingList){
			Driver driver = trip.getDriver();
			Cab cab = driver.getCab();
			cab.setAvailable(true);
			driverRepository2.save(driver);
			trip.setStatus(TripStatus.CANCELED);
		}
		//Now we will delete the customer from the repository and as a result of cascading effect trips will also
		//be deleted
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Driver driver = null;
		List<Driver> driverList = driverRepository2.findAll();
		for(Driver currDriver : driverList){
			if(currDriver.getCab().getAvailable()){
				if(driver== null || currDriver.getDriverId() < driver.getDriverId()){
					driver =  currDriver;
				}
			}
		}
		if(driver == null){
			throw new Exception("No cab available!");
		}
		Customer customer = customerRepository2.findById(customerId).get();
		TripBooking tripBooking = new TripBooking();
		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		driver.getCab().setAvailable(false);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		int rate = driver.getCab().getPerKmRate();
		tripBooking.setBill(distanceInKm*rate);

		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

//		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking completeTrip = tripBookingRepository2.findById(tripId).get();
		completeTrip.setStatus(TripStatus.COMPLETED);
		completeTrip.getDriver().getCab().setAvailable(true);

//		int dist = completeTrip.getDistanceInKm();
//		int cabPrice = completeTrip.getDriver().getCab().getPerKmRate();
//		int bill = dist * cabPrice;
//		completeTrip.setBill(bill);

		tripBookingRepository2.save(completeTrip);
	}
}
