package com.ajcp.springboot.reactor.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioComentarios {
	
	private Usuario usuario;
	private Comentarios comentarios;

}
