package api.pojo.response;

import lombok.Data;

@Data
public class CreateBookingResponse {
	
	 public String firstname;
	    public String lastname;
	    public int totalprice;
	    public boolean depositpaid;
	    public Bookingdates bookingdates;
	    public String additionalneeds;
@Data    
public static class Bookingdates{
	        public String checkin;
	        public String checkout;
	    }

}
