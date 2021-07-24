package com.ajcp.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

@ToString
public class Comentarios {
	
	private List<String> comentarios;

	public Comentarios() {
		this.comentarios = new ArrayList<>();
	}

	public List<String> getComentarios() {
		return comentarios;
	}

	public void addComentario(String comentario) {
		this.comentarios.add(comentario);
	}

}
