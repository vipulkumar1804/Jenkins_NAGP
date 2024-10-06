package api.pojos.request;

import lombok.Data;

@Data
public class CreateBooking {
	public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;


@Data
public static class BookingDates {
	
	    public String checkin;
	    public String checkout;
	}
}