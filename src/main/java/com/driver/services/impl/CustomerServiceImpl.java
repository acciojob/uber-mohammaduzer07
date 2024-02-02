package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.*;

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
//		Customer customer = customerRepository2.findById(customerId).get();
//		List<TripBooking> bookingList = customer.getTripBookingList();
//
//		//Now we will set the cab as available for each and every trip booked by this customer,
//		//who is going to be deleted
//		for(TripBooking trip : bookingList){
//			Driver driver = trip.getDriver();
//			Cab cab = driver.getCab();
//			cab.setAvailable(true);
//			driverRepository2.save(driver);
//			trip.setStatus(TripStatus.CANCELED);
//		}
//		//Now we will delete the customer from the repository and as a result of cascading effect trips will also
//		//be deleted
//		customerRepository2.delete(customer);
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Driver driver = null;
		List<Driver> driverList = driverRepository2.findAll();
		Collections.sort(driverList, (a,b)->{
			return a.getDriverId() - b.getDriverId();
		});
		for(Driver currDriver : driverList){
			if(currDriver.getCab().getAvailable()){
					driver =  currDriver;
					break;
			}
		}
		if(driver == null){
			throw new Exception("No cab available!");
		}
		Customer customer = customerRepository2.findById(customerId).get();
		int bill = driver.getCab().getPerKmRate()*distanceInKm;
		TripBooking tripBooking = new TripBooking(fromLocation,toLocation,distanceInKm,TripStatus.CONFIRMED,bill,customer,driver);
		driver.getCab().setAvailable(false);
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);


		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

		tripBookingRepository2.save(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		Driver driver = tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		driverRepository2.save(driver);
//		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking completeTrip = tripBookingRepository2.findById(tripId).get();
		completeTrip.setStatus(TripStatus.COMPLETED);
		Driver driver = completeTrip.getDriver();
		driver.getCab().setAvailable(true);
//		completeTrip.getDriver().getCab().setAvailable(true);
		driverRepository2.save(driver);

//		int dist = completeTrip.getDistanceInKm();
//		int cabPrice = completeTrip.getDriver().getCab().getPerKmRate();
//		int bill = dist * cabPrice;
//		completeTrip.setBill(bill);

		tripBookingRepository2.save(completeTrip);
	}
}
