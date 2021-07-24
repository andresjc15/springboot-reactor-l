package com.ajcp.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ajcp.springboot.reactor.app.models.Comentarios;
import com.ajcp.springboot.reactor.app.models.Usuario;
import com.ajcp.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringbootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringbootReactorApplication.class); 
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ejemploContraPresion();
	}
	
	public void ejemploContraPresion() {
		Flux.range(1, 10)
		.log()
		.limitRate(5)
		.subscribe(/*new Subscriber<Integer>() {
			
			private Subscription s;
			
			private Integer limite = 2;
			private Integer consumido = 0;

			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				s.request(limite);
			}

			@Override
			public void onNext(Integer t) {
				log.info(t.toString());
				consumido++;
				if (consumido == limite) {
					consumido = 0;
					s.request(limite);
				}
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			}
		}*/);
	}
	
	public void ejemploIntervaloDesdeCreate() {
		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				
				private Integer contador = 0;
				
				@Override
				public void run() {
					emitter.next(++contador);
					if (contador == 10) {
						timer.cancel();
						emitter.complete();
					}
					
					if (contador == 15) {
						timer.cancel();
						emitter.error(new InterruptedException("Error, se ha detenido el flux"));
					}
				}
			}, 1000, 1000);
		})
		.subscribe(next -> log.info(next.toString()),
				error -> log.error(error.getMessage()),
				() -> log.info("Hemos terminado!!"));
		
	}
	
	public void ejemploIntervalInfinito() throws InterruptedException {
		
		CountDownLatch latch = new CountDownLatch(1);
		
		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(latch::countDown)
		.flatMap(i -> {
			if ( i >= 5 ) {
				return Flux.error(new InterruptedException("Solo hasta 5!"));
			}
			return Flux.just(i);
		})
		.map( i -> "Hola " + i)
		.retry(2)
		.subscribe(s -> log.info(s), e -> log.error(e.getMessage()));
		
		latch.await();
	}
	
	public void ejemplosDelayElements() throws InterruptedException {
		 Flux<Integer> rango = Flux.range(1, 12)
		 		.delayElements(Duration.ofSeconds(0))
		 		.doOnNext( i -> log.info(i.toString()));
		 
		 rango.blockLast();
		 
		 Thread.sleep(130000);
	 }
	
	public void ejemplosInterval() {
		 Flux<Integer> rango = Flux.range(1, 12);
		 Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		 
		 rango.zipWith(retraso, (ra, re) -> ra)
		 .doOnNext( i -> log.info(i.toString()))
		 .blockLast();
	 }
	
	 public void ejemploZipWithRanges() {
		 Flux<Integer> rangos = Flux.range(0, 4);
		 Flux.just(1,2,3,4)
		 .map( i -> (i*2))
		 .zipWith(rangos, (uno, dos) -> String.format("Primer flux: %d, Segundo flux: %d", uno, dos))
		 .subscribe(texto -> log.info(texto));
	 }
	
	public void ejemploUsuarioComentariosZipWithForma2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable( () -> new Usuario("Jhon"	, "Doe"));
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable( () -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola que tal");
			comentarios.addComentario("Todo bien?");
			comentarios.addComentario("uwu");
			return comentarios;
		});
		
		Mono<UsuarioComentarios> usuarioConComentarios =  usuarioMono
				.zipWith(comentariosUsuarioMono)
				.map(tuple -> {
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					return new UsuarioComentarios(u, c);
				});
			usuarioConComentarios.subscribe( uc -> log.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable( () -> new Usuario("Jhon"	, "Doe"));
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable( () -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola que tal");
			comentarios.addComentario("Todo bien?");
			comentarios.addComentario("uwu");
			return comentarios;
		});
		
		Mono<UsuarioComentarios> usuarioConComentarios =  usuarioMono
				.zipWith(comentariosUsuarioMono, (usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));
			usuarioConComentarios.subscribe( uc -> log.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosFlatMap() {
		Mono<Usuario> usuarioMono = Mono.fromCallable( () -> new Usuario("Jhon"	, "Doe"));
		
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable( () -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola que tal");
			comentarios.addComentario("Todo bien?");
			comentarios.addComentario("uwu");
			return comentarios;
		});
		
		usuarioMono.flatMap( u -> comentariosUsuarioMono.map( c -> new UsuarioComentarios(u, c)))
			.subscribe( c -> log.info(c.toString()));
	}
	
	public void ejemploCollecttList() throws Exception {
		
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "C"));
		usuariosList.add(new Usuario("Andrea", "T"));
		usuariosList.add(new Usuario("Jose", "Vilca"));
		usuariosList.add(new Usuario("Isabel", "P"));
		usuariosList.add(new Usuario("Claudia", "G"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		
		Flux.fromIterable(usuariosList)
		.collectList()
		.subscribe(lista -> {
			lista.forEach(item -> log.info(item.toString()));
		});
	}
	
	public void ejemploToString() throws Exception {
		
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "C"));
		usuariosList.add(new Usuario("Andrea", "T"));
		usuariosList.add(new Usuario("Jose", "Vilca"));
		usuariosList.add(new Usuario("Isabel", "P"));
		usuariosList.add(new Usuario("Claudia", "G"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		
		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("bruce".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				})
				.map(nombre -> {
					return nombre.toLowerCase();
				})
				.subscribe(e -> log.info(e.toString()));
	}
	
	public void ejemploFlatMap() throws Exception {
		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres C");
		usuariosList.add("Andrea T");
		usuariosList.add("Jose Vilca");
		usuariosList.add("Isabel P");
		usuariosList.add("Claudia G");
		usuariosList.add("Bruce Lee");
		
		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				})
				.subscribe(e -> log.info(e.toString()));
	}
	
	public void ejemploIterable() throws Exception {
		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres C");
		usuariosList.add("Andrea T");
		usuariosList.add("Jose Vilca");
		usuariosList.add("Isabel P");
		usuariosList.add("Claudia G");
		usuariosList.add("Bruce Lee");
		
		Flux<String> nombres = Flux.fromIterable(usuariosList); // Flux.just("Andres C","Jose Vilca","Andrea T","Isabel P","Claudia G", "Bruce Lee", "Fiorela Trujillo");
				
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
				.doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no pueden ser vacios");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				})
				.map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					String apellido = usuario.getApellido().toLowerCase();
					usuario.setNombre(nombre);
					usuario.setApellido(apellido);
					return usuario;
				});
		
		usuarios.subscribe(e -> log.info(e.toString()),
				error -> log.error(error.getMessage()),
				new Runnable() {
					
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable con éxito!!");
					}
				});
	}

}
