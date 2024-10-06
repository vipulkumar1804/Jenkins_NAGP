package api.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import dataProvider.TData;
import enums.ParametersResource;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import static io.restassured.RestAssured.given;

public class APIUtil {

	public static ThreadLocal<RequestSpecification> req = new ThreadLocal<>();
	private static ThreadLocal<String> token = new ThreadLocal<>();
	private static ThreadLocal<String> method = new ThreadLocal<>();
	private static ThreadLocal<LinkedHashMap<String, Response>> response = ThreadLocal.withInitial(LinkedHashMap::new);
	private static ThreadLocal<String> latestExecutedAPI = new ThreadLocal<>();
	private static final Logger s_logger = LoggerFactory.getLogger(APIUtil.class);
	public static ThreadLocal<String>logFilePath= new ThreadLocal<String>();
	public  static ThreadLocal<RequestSpecification> reqWeb=new ThreadLocal<>();

	public static String getMethod() {
		return method.get();
	}

	public static void setMethod(String methodName) {
		method.set(methodName);
	}

	public static LinkedHashMap<String, Response> getAPIResponse() {
		return response.get();
	}

	public static void setAPIResponse(LinkedHashMap<String, Response> res) {
		List<LinkedHashMap<String, Response>> resourceResponse = new ArrayList<>();
		resourceResponse.add(res);
		String resourcePoint = res.keySet().toString().replaceAll("\\]", "").replaceAll("\\[", "").trim();
		response.get().put(resourcePoint, res.get(resourcePoint));
	}

	public static String getlatestExecutedAPI() {
		return latestExecutedAPI.get();
	}

	public static void setlatestExecutedAPI(String api) {
		latestExecutedAPI.set(api);
	}

	public static RequestSpecification getRequest() {
		return req.get();
	}

	public static void setRequest(RequestSpecification api) {
		req.set(api);
	}

	public static Response getResponse() {
		return getAPIResponse().get(getLatestExecutedAPI());
	}

	public static String getTokenKey() {
		return token.get();
	}

	public static void setToken(String tokenValue) {
		token.set(tokenValue);
	}

	public static String getLatestExecutedAPI() {
		return getlatestExecutedAPI();
	}
	public static RequestSpecification getRequestWeb() {
		return reqWeb.get();
	}
	public static void setRequestWeb(RequestSpecification api) {
		   reqWeb.set(api);
		}

	public void verifyStatusCode(String msg, int code) throws Exception {
		try {
			int statusCode = getStatusCode();
			Assert.assertEquals(statusCode, code);
		} catch (Throwable ex) {
			throw new Exception("Status code is not correct" + ex.getMessage());
		}
	}

	public static int getStatusCode() throws Exception {
		try {
			int statusCode = getResponse().getStatusCode();
			return statusCode;
		} catch (Throwable ex) {
			throw new Exception("error while getting status code for API" + getlatestExecutedAPI());
		}
	}

	public static void verifyJsonElement(String key, String resource, String expected) throws Exception {
		try {
			String actual = getJsonValue(key, resource);
			Assert.assertEquals(true, actual.contains(expected), "Expected Data " + expected + " for attribute " + key
					+ "is not matches with actual data " + actual);
		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public static void verifyJsonElement(String key, String expected) throws Exception {
		try {
			String actual = getJsonValue(key);
			Assert.assertEquals(true, actual.toLowerCase().contains(expected.toLowerCase()), "Expected Data: "
					+ expected + " for attribute " + key + " is not matches with actual data: " + actual);
		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public static String getJsonValue(String key, String resource) throws Exception {
		try {
			key = key.replaceAll(" ", ".");
			String actual = getJsonString(getAPIResponse().get(resource), key);
			actual = actual.replaceAll("\\[", "").replaceAll("\\]", "").trim();
			return actual;
		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public static String getJsonValue(String key) throws Exception {
		try {
			key = key.replaceAll(" ", ".");
			String actual = getJsonString(getResponse(), key);
			actual = actual.replaceAll("\\[", "").replaceAll("\\]", "").trim();
			return actual;
		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public static String getJsonString(Response response, String key) throws Exception {
		try {
			String resp = response.asString();
			JsonPath js = new JsonPath(resp);
			return js.get(key).toString();
		} catch (Exception ex) {
			throw new Exception("Unable to get attribute " + key);
		}
	}

	public static int getJsonInt(String key) throws Exception {
		try {
			String resp = getResponse().asString();
			JsonPath js = new JsonPath(resp);
			return js.getInt(key);
		} catch (Exception ex) {
			throw new Exception("Unable to get integer value for json attribute " + key);
		}
	}

	public static void callAPI(String resource, String method, RequestSpecification reqQuery, String pathParameter)
			throws Exception {
		LinkedHashMap<String, Response> resMap = new LinkedHashMap<String, Response>();
		try {

			String URLPath = createAPIResource(resource, pathParameter);
			setlatestExecutedAPI(resource);
			if (method.equalsIgnoreCase("POST") && !resource.contains("auth")) {
				resMap.put(resource,
						given().spec(reqQuery).header("Authorization", getTokenKey()).when().log().all().post(URLPath));

				// Printing response
				System.out.println("Response :: " + resMap.get(resource).getBody().asString());

			} else if (method.equalsIgnoreCase("GET")) {
				resMap.put(resource,
						given().spec(reqQuery).header("Authorization", getTokenKey()).when().log().all().get(URLPath));

			} else if (method.equalsIgnoreCase("PUT")) {
				resMap.put(resource,
						given().spec(reqQuery).auth().preemptive().basic(TData.ADMIN, TData.PASSWORD).when().log().all().put(URLPath));

			} else if (method.equalsIgnoreCase("DELETE")) {
				resMap.put(resource, given().spec(reqQuery).auth().preemptive().basic(TData.ADMIN, TData.PASSWORD).when().log().all()
						.delete(URLPath));
			} 
			else if (method.equalsIgnoreCase("PATCH")) {
				resMap.put(resource, given().spec(reqQuery).auth().preemptive().basic(TData.ADMIN, TData.PASSWORD).when().log().all()
						.patch(URLPath));
			}else
				resMap.put(resource, given().spec(reqQuery).when().log().all().post(URLPath));
			setAPIResponse(resMap);
		} catch (Throwable ex) {
			throw new Exception("Failed while processing API " + getlatestExecutedAPI() + " " + ex.getMessage());
		}
	}
	public static String createAPIResource(String resource,String pathParameter) throws Exception {
		   try {
		   
		       if (resource.contains("BOOKINGID"))
		         return resource.replaceAll("BOOKINGID", pathParameter);
		      else
		         return resource;
		   }catch ( Exception ex){
		      throw new Exception("Exception while creating endpoint for resource "+resource +" pointing to path "+pathParameter);
		   }
		   
		}
	
	/** 
	 * This method is specific to call Web API
	 * @param m - API Method
	 * @param logFlag- Log Flag
	 */
	
	public static void requestSpecWeb(Method m,String logFlag)  {
		   try {
		      setBaseURI(logFlag,ParametersResource.URI.getParameter(),m);
		      setMethod(m.getName());
		   }catch (Exception ex){
			   s_logger.info("Exception while setting up baseURI and Test Name-->"+ex.getMessage());
		      Assert.assertTrue(false,"Exception while setting up baseURI and Test Name-->"+ex.getMessage());
		   }
		}
	
	/**
	 * This method will set BASE URI for WEB API
	 * @param apiLogFlag- API log flag
	 * @param uri- URI
	 * @param m - API Method
	 * @throws Exception
	 */
	public static void setBaseURI(String apiLogFlag,String uri,Method m) throws Exception {
		   if(apiLogFlag.equalsIgnoreCase("yes")){
		      PrintStream log = logger(m);
		      setRequestWeb(new RequestSpecBuilder().setBaseUri(uri)
		            .setContentType(ContentType.JSON)
		            .addFilter(RequestLoggingFilter.logRequestTo(log)).addFilter(ResponseLoggingFilter.logResponseTo(log)).build());
		      clearTheFile(logFilePath.get());
		   }else{
		      setRequestWeb(new RequestSpecBuilder().setBaseUri(uri)
		            .setContentType(ContentType.JSON)
		            .build());
		   }
		}
	public static PrintStream logger(Method m) throws Exception {
		   try {
		      String featureName = m.getName();
		      String scenarioName = "log";
		      String logFileFolder = new File(System.getProperty("user.dir") + File.separator+"src"+File.separator+"test"+File.separator+"resources" + File.separator + "OutputData" + File.separator + featureName
		            + File.separator).getAbsolutePath();

		      File fileLocation = new File(logFileFolder);
		      if (!fileLocation.exists())
		         new File(logFileFolder).mkdirs();

		      logFilePath.set(logFileFolder + "/" + scenarioName + ".txt");
		      System.out.println(logFilePath.get());

		      return new PrintStream(new FileOutputStream(logFilePath.get()));
		   }catch (Exception ex){
		      throw new Exception("Exception while setting up log --->"+ex.getMessage());
		   }
		}
	public static void clearTheFile(String filePath) throws Exception {
		   try {
		      FileWriter fwOb = new FileWriter(filePath, false);
		      PrintWriter pwOb = new PrintWriter(fwOb, false);
		      pwOb.flush();
		      pwOb.close();
		      fwOb.close();
		   }catch (Exception ex){
		      throw new Exception("Exception while clearing the log files-->"+ex.getMessage());
		   }
		   }
	public void logFail(String stepName, Exception ex) {
		Assert.assertTrue(false,ex.getMessage());
	}
	public static boolean getKey(Response response, String key) {
		   try {
		      String resp = response.asString();
		      JsonPath js = new JsonPath(resp);
		      if (js.get(key.replaceAll(" ", ".")).toString().trim().contains("null")) {
		         return false;
		      } else {
		         return true;
		      }
		   } catch (Exception e) {
		      return false;
		   }
		}

		public static String generateNumber() {
		   String numbers = "0123456789";
		   Random rand = new Random();
		   StringBuilder rnd = new StringBuilder(7);
		   for (int i = 0; i < 10; i++) {
		      rnd.append(numbers.charAt(rand.nextInt(numbers.length())));
		   }
		   return rnd.toString();
		}
	

}
