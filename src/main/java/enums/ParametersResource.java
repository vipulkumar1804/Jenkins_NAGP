package enums;

public enum ParametersResource {
	UserName("admin"),
	PassWord("password123"),
	URI("https://restful-booker.herokuapp.com");
	
	
	private String parameter;
	
	ParametersResource(String parameter){
		this.parameter=parameter;
	}
	
	public String getParameter() {
		return parameter;
	}

}
