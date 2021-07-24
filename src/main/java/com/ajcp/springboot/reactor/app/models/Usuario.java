package com.ajcp.springboot.reactor.app.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String nombre;
	private String apellido; 
	
}
