package api.methods;

import api.pojo.response.CreateBookingResponse;
import api.pojos.request.CreateBooking;
import api.pojos.request.PartialBooking;
import api.utils.APIUtil;
import dataProvider.TData;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.gson.Gson;

public class BookingMethods extends APIUtil {
	
	private static final Logger s_logger = LoggerFactory.getLogger(BookingMethods.class);
	Gson gson = new Gson();
	
	public RequestSpecification getBookingRequest() {
		
		return given(getRequestWeb());
	}
	public RequestSpecification createBookingBody(String fName,String lName, String additionalNeed) {
		CreateBooking booking = new CreateBooking();
		CreateBooking.BookingDates dates = new CreateBooking.BookingDates();
		booking.setFirstname(fName);
		booking.setLastname(lName);
		booking.setAdditionalneeds(additionalNeed);
		booking.setTotalprice(Integer.parseInt(TData.TOTAL_PRICE));
		booking.setDepositpaid(Boolean.parseBoolean(TData.DEPOSIT_PAID));
		dates.setCheckin(TData.CHECK_IN);
		dates.setCheckout(TData.CHECK_OUT);
		booking.setBookingdates(dates);
		return given(getRequestWeb()).body(booking);
		
	}
	public RequestSpecification createPartialBookingBody(String fName,String lName, String additionalNeed) {
		PartialBooking booking = new PartialBooking();
		booking.setFirstname(fName);
		booking.setLastname(lName);
		booking.setAdditionalneeds(additionalNeed);
		return given(getRequestWeb()).body(booking);
		
	}
	
	public void getAllBooking(String method, String resource) {
		String stepName = "Fetch all the booking IDs";
		try {
			callAPI(resource, method,getBookingRequest(),"");
			verifyStatusCode(stepName,200);
			s_logger.info("Validated status code");
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
		
	}
	public RequestSpecification getBookingIDRequest() {
		return given(getRequestWeb());
	}
	public Map<String,String> createBooking(String fName,String lName, String additionalNeed,String method, String resource) {
		String stepName = "Create new booking";
		s_logger.info(stepName);
		Map<String, String>bookingId= new HashMap<>();
		try {
			callAPI(resource, method,createBookingBody(fName,lName,additionalNeed),"");
			verifyStatusCode(stepName,200);
			s_logger.info("Validated status code");
			verifyJsonElement("booking firstname", fName);
			verifyJsonElement("booking lastname", lName);
			verifyJsonElement("booking totalprice", TData.TOTAL_PRICE);
			verifyJsonElement("booking depositpaid", TData.DEPOSIT_PAID);
			verifyJsonElement("booking bookingdates checkin", TData.CHECK_IN);
			verifyJsonElement("booking bookingdates checkout", TData.CHECK_OUT);
			bookingId.putAll(addBookingID());
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
		return bookingId;
	}
	public Map<String,String> addBookingID() throws Exception{
		Map<String, String>bookingId= new HashMap<>();
		String id=getJsonValue("bookingid");
		bookingId.put("booking_id", id);
		return bookingId;
	}
	public void getCreatedBooking(String method, String resource,String bookingId) {
		String stepName="Get the booking ID";
		try {
			callAPI(resource, method,getBookingIDRequest(),bookingId);
			verifyStatusCode("Validate status code for Get booking ID response",200);
			
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	public void validateCreatedBookingDetails(String firstName,String lastName,String additional) {
		String stepName="Validate created booking ID details in the Get booking ID response";
		try {
			CreateBookingResponse createBookigReponse = gson.fromJson(getResponse().getBody().asString(),CreateBookingResponse.class);
		boolean fName=	createBookigReponse.getFirstname().equals(firstName);
		s_logger.info("Validated the first name in booking ID response as :"+createBookigReponse.getFirstname());
		Assert.assertTrue(fName, "Validation failed for first name in the booking ID response");
		boolean lName=	createBookigReponse.getLastname().equals(lastName);
		s_logger.info("Validated the last name in booking ID response as :"+createBookigReponse.getLastname());
		Assert.assertTrue(lName, "Validation failed for last name in the booking ID response");
		boolean additionalNeed=	createBookigReponse.getAdditionalneeds().equals(additional);
		s_logger.info("Validated the additional needs in booking ID response as :"+createBookigReponse.getAdditionalneeds());
		Assert.assertTrue(additionalNeed, "Validation failed for additional needs in the booking ID response");			
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	public void deleteCreatedBooking(String method, String resource,String bookingId) {
		String stepName="Delete the created booking ID";
		try {
			callAPI(resource, method,getBookingIDRequest(),bookingId);
			verifyStatusCode("Validate status code for delete booking ID response",201);			
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	public void updateBookingID(String fName,String lName, String additionalNeed,String method, String resource,String bookingId) {
		String stepName = "Update created new booking and validate";
		try {
			callAPI(resource, method,createBookingBody(fName,lName,additionalNeed),bookingId);
			verifyStatusCode(stepName,200);
			s_logger.info("Validated status code");
			verifyJsonElement("firstname", fName);
			verifyJsonElement("lastname", lName);
			verifyJsonElement("additionalneeds", additionalNeed);
			verifyJsonElement("bookingdates checkin", TData.CHECK_IN);
			s_logger.info("Validated update booking details");
			
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	public void partialBookingUpdate(String fName,String lName, String additionalNeed,String method, String resource,String bookingId) {
		String stepName = "Partial Update created new booking and validate";
		try {
			callAPI(resource, method,createPartialBookingBody(fName,lName,additionalNeed),bookingId);
			verifyStatusCode(stepName,200);
			s_logger.info("Validated status code");
			verifyJsonElement("firstname", fName);
			verifyJsonElement("lastname", lName);
			verifyJsonElement("additionalneeds", additionalNeed);
			verifyJsonElement("bookingdates checkin", TData.CHECK_IN);
			s_logger.info("Validated details in the partial booking API response");
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	


}
