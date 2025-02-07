package in.hashconnect.vo;

import java.util.Arrays;
import java.util.Optional;

public enum RequestTemplateTypes {
	INVOICE("INVOICE"),
	ORDER("ORDER"),
	NOT_FOUND("NOT_FOUND");
	
	private final String type;
	RequestTemplateTypes(String type) {
		this.type = type;
	}
	
	public static RequestTemplateTypes getName(String type) {
		Optional<RequestTemplateTypes> name =  Arrays.stream(RequestTemplateTypes.values())
	            .filter(requestTemplateTypes -> requestTemplateTypes.type.equals(type) 
	                || requestTemplateTypes.type.equals(type))
	            .findFirst();
		if (name.isEmpty()) {
			return RequestTemplateTypes.NOT_FOUND;
		}
	
		return name.get();
	}
}
