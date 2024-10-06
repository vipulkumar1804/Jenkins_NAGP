package api.methods;

import api.pojos.request.CreateTokenBody;
import api.utils.APIUtil;
import enums.ParametersResource;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class CreateTokenMethod extends APIUtil {
	
	public RequestSpecification createToken() {
		CreateTokenBody credentials = new CreateTokenBody();
		credentials.setUsername(ParametersResource.UserName.getParameter());
		credentials.setPassword(ParametersResource.PassWord.getParameter());		
		return given(getRequestWeb()).body(credentials);
		
	}
	
	public void generateToken(String method, String resource) {
		String stepName = "Generate token for the booking";
		try {
			callAPI(resource, method,createToken(),"");
			verifyLoginAndSaveToken(stepName,200,getToken());
			
		}catch(Exception ex) {
			logFail(stepName,ex);
		}
	}
	
	//Verify the auth token and save it
	
	public void verifyLoginAndSaveToken(String msg, int statusCode, String token) throws Exception {
		verifyStatusCode(msg,statusCode);
		setToken(token);
		
	}
	
	//Gets the token value
	
	public String getToken() throws Exception {
		return getJsonValue("token");
	}
	

}
