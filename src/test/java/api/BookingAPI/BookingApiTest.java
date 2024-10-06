package api.BookingAPI;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import api.methods.BookingMethods;
import api.methods.CreateTokenMethod;
import api.utils.APIUtil;
import dataProvider.DataProviderUtils;
import enums.APIResource;
import enums.Request;

public class BookingApiTest extends APIUtil {

	CreateTokenMethod token;
	BookingMethods booking;
	String stepName="";

	BookingApiTest(){
		token = new CreateTokenMethod();
		booking =  new BookingMethods();

	}
	
	@BeforeMethod
	@Parameters({"apilogs"})
	public void nameBefore(Method method, String apilog) {
		requestSpecWeb(method, apilog);
		
	}
	
	@Test(description="Hit the booking API and validate the status")
	public void getAllBookingID() {
		stepName="Generate the token";
		token.generateToken(Request.POST,APIResource.authToken);
		stepName="Hit the booking request and validate the response";
		booking.getAllBooking(Request.GET, APIResource.booking);		
	}
	
	@Test(description="Create booking and verify in the booking API and delete",dataProvider = "DataProvider",dataProviderClass = DataProviderUtils.class)
	public void createBooking(String firstName, String lastName, String additionalNeed) {
		Map<String,String> testInfo = new HashMap<>();
		token.generateToken(Request.POST,APIResource.authToken);
		stepName="Create the booking and validate response";
		testInfo.putAll(booking.createBooking(firstName, lastName, additionalNeed, Request.POST, APIResource.createBooking));
		booking.getCreatedBooking(Request.GET,APIResource.bookingID,testInfo.get("booking_id"));
		stepName="Validated the created booking details in the Get booking ID response ";
		booking.validateCreatedBookingDetails(firstName, lastName, additionalNeed);
		stepName="Delete created booking";
		booking.deleteCreatedBooking(Request.DELETE,APIResource.deleteBooking,testInfo.get("booking_id"));
				
	}
	@Test(description="Update the booking and verify in the booking API and delete",dataProvider = "DataProvider",dataProviderClass = DataProviderUtils.class)
	public void updateBooking(String firstName, String lastName, String additionalNeed) {
		Map<String,String> testInfo = new HashMap<>();
		token.generateToken(Request.POST,APIResource.authToken);
		testInfo.putAll(booking.createBooking(firstName, lastName, additionalNeed, Request.POST, APIResource.createBooking));
		booking.getCreatedBooking(Request.GET,APIResource.bookingID,testInfo.get("booking_id"));
		booking.validateCreatedBookingDetails(firstName, lastName, additionalNeed);
		stepName="Update the booking details";
		booking.updateBookingID(firstName, lastName, additionalNeed, Request.PUT,APIResource.updateBooking,testInfo.get("booking_id"));
		booking.deleteCreatedBooking(Request.DELETE,APIResource.deleteBooking,testInfo.get("booking_id"));
		
				
	}
	@Test(description="Update the partial booking and verify in the booking API and delete",dataProvider = "DataProvider",dataProviderClass = DataProviderUtils.class)
	public void updatePartialBooking(String firstName, String lastName, String additionalNeed) {
		Map<String,String> testInfo = new HashMap<>();
		token.generateToken(Request.POST,APIResource.authToken);
		testInfo.putAll(booking.createBooking(firstName, lastName, additionalNeed, Request.POST, APIResource.createBooking));
		booking.getCreatedBooking(Request.GET,APIResource.bookingID,testInfo.get("booking_id"));
		booking.validateCreatedBookingDetails(firstName, lastName, additionalNeed);
		stepName="Partially update the booking details";
		booking.partialBookingUpdate(firstName, lastName, additionalNeed, Request.PATCH,APIResource.partialUpdateBooking,testInfo.get("booking_id"));
		booking.deleteCreatedBooking(Request.DELETE,APIResource.deleteBooking,testInfo.get("booking_id"));
		
				
	}
	
}
