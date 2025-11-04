package org.cuatrovientos.dam.psp.hilos.tamagochi;

import java.util.Random;
import java.util.Scanner;

public class Tamagochi implements Runnable{
	private String nombreTama;
	private EstadoTamagochi estadoTama;
	private int nivelSuciedad;
	private boolean vivo;
	private Random rnd;
	private int resultadoJuegoCorrecto;
    private Scanner scannerDelCuidador;

	private static final long TIEMPO_TARDA_ENSUCIARSE = 10000;
	private static final long TIEMPO_VIDA_MAX = 300000;
	private static final long TIEMPO_LIMPIEZA = 5000;
	private static final int NUMERO_MIN_GENERAR_RANDOM = 1;
	private static final int NUMERO_MAX_GENERAR_RANDOM = 10;
	private static final long INTERVALO_CHEQUEO = 1000; 
	private static final int CHEQUEOS_PARA_ENSUCIAR = (int)(TIEMPO_TARDA_ENSUCIARSE / INTERVALO_CHEQUEO);
	
	
	
	//TODO
	/**
	 * TENGO QUE COGER TODOS LOS METODOS PRINCIPALES (JUGAR, LIMPIARSE, COMER, MORIR)
	 * HACER QUE SIMPLEMENTE LES CAMBIE EL ESTADO A SU ESTADO SI NO ESTAN REALIZANDO NINGUNA ACCION
	 * EN EL RUN HACER IFS PARA TODOS LOS ESTADOS Y AHI LLAMAR A OTRO METODO PRIVADO SACADA
	 * CON EL CODIGO DE LOS METODOS ANTERIORES. ENTONCES TAMBIEN TENGO QUE LLAMAR AL RUN 
	 * EN EL CUIDADOR Y SIMPLEMENTE EJEMPLO: DECIR TAMA.JUGAR() ENTONCES LE CAMBIARA EL ESTADO SI PUEDE
	 * Y SI ES ASI, ESTARA REVISANDO EL RUN SIEMPRE Y MIRARA QUE SU ESTADO A CAMBIADO A JUGAR
	 * ENTONCES LLAMARA AL METODO PRIVADO QUE REALIZA JUGAR
	 */
	
	public Tamagochi(String nombreTama) {
		this.nombreTama = nombreTama;
		asignarEstadoEsperando();
        this.nivelSuciedad = 0;
        this.vivo  = true;
        this.rnd = new Random();
	}
	
	public String getNombreTama() {
        return nombreTama;
    }

	public EstadoTamagochi getEstado() {
        return estadoTama;
    }

    public int getSuciedad() {
        return nivelSuciedad;
    }

    public boolean isVivo() {
        return vivo;
    }

	 public void setScannerDelCuidador(Scanner scanner) {
	     this.scannerDelCuidador = scanner;
	 }
    
	@Override
	public void run() {

		mostrarMensajeTamaHaSidoCreado();

		long tiempoInicioVida = System.currentTimeMillis();

		while(vivo) {

			try {
	
				
				if (comprobarTamaEstaEsperandoYVivo()) {
					
					Thread.sleep(TIEMPO_TARDA_ENSUCIARSE);
			
					incrementarNivelSuciedadSiNoSeEstaLimpiando();
				}
				
				if (comprobarTamaEstaComiendo()) {
					alimentarse("manzana");
				}
				
				if (comprobarTamaEstaLimpiandose()) {
					limpiar();
				}
				
				if (comprobarTamaEstaJugando()) {
					jugar();
				}
				
				comprobarNivelDeSuciedadConLimiteEstablecido();
			
				comprobarTiempoDeVidaRestante(tiempoInicioVida);
	
			} catch (InterruptedException e) {
	
				mostrarMensajeEInterrumpirTamaYInterrumpirThread();
		
			}

		}


		cambiarEstadoAMuertoYMostrarMensaje();

	}
	
	
	public void comprobarEstadoYCambiaraAlimentarse() {
		
		if (comprobarTamaEstaEsperandoYVivo()) {
			
			cambiarEstadoComer();
            
        } else {
        	
            mostrarMensajeTamaNoPuedeLimpiarseEstaOcupado();
            
        }
		
	}
	
	private void alimentarse(String comida) {
		System.out.println("-> " + nombreTama + " EMPIEZA de comer " + comida);

		int tiempoComiendo = generarTiempoTardaEnComer();
		
		try {
			
			Thread.sleep(tiempoComiendo);
			
		}catch (InterruptedException e) {
			
            Thread.currentThread().interrupt();
            
        }
		
		mostrarMensajeTamaTerminaComer(comida);
		
        asignarEstadoEsperando();
	}

	public void comprobarEstadoyCambiarEstadoalimpiarse() {
		
		if (comprobarTamaEstaEsperandoYVivo()) {
			
			cambiarEstadoALimpiarse();
	        
		}else {
			
            mostrarMensajeTamaNoPuedeLimpiarseEstaOcupado();
            
		}
	}
	
	private void limpiar() {
		
		try {
			
			limpiarTamagochi();

		} catch (InterruptedException e) {
			
			mostrarMensajeEInterrumpirLimpieza();
			
		}
		
		mostrarMensajeLimpiezaFinalizada();
		
        asignarEstadoEsperando(); 
        
	}
	
	
	public void comprobarEstadoYCambiarEstadoaJugando() {
		if (this.estadoTama != EstadoTamagochi.ESPERANDO && this.estadoTama != EstadoTamagochi.JUGANDO) {	    
	        System.out.println(mostrarMensajeTamaNoPuedeJugarEstaOcupado());
	        return; 
	    }
	    
	    cambiarEstadoAJugando();
		
	}
	
	private void jugar() {
		 mostrarMensajeTamaComienzaAJugar();
		    
		    int primerNumeroSuma = generarPrimerNumeroRandomSumaJugar(); 
		    int segundoNumeroSuma = generarSegundoNumeroRandomSumaJugar(primerNumeroSuma); 
		    resultadoSumaDeDosNumerosJugar(primerNumeroSuma, segundoNumeroSuma);
		    
		    System.out.print(primerNumeroSuma + " + " + segundoNumeroSuma + " = "); 
		    
		    boolean respuestaEsCorrecta = false;
		    try {
		        respuestaEsCorrecta = comprobarRespuestaSuma(Integer.parseInt(this.scannerDelCuidador.nextLine()));
		    } catch (NumberFormatException e) {
		        System.out.println("Respuesta no numérica. Se considera incorrecta.");
		    }
		    
		    if (respuestaEsCorrecta) {
		        
		        mensajeRespuestaCorrectaAJugar();
		        asignarEstadoEsperando();
		        
		    } else {
		        
		        mensajeRespuestaIncorrectaAJugar();
		    }
	}
	
	
	public boolean matar() {
		
		if (comprobarTamaEstaEsperandoYVivo()) {
			
			return matarTamagochi(); 
	        
	    } else if (!this.vivo) {
	    	
	         return tamaYaEstabaMuerto();
	         
	    } else {
	    	
	        return tamaNoPuedeDestruirseEstaRealizandoOtraAccion();
	    }
	}

	private boolean matarTamagochi() {
		
		System.out.println("!!! DESTRUCCIÓN INICIADA " + nombreTama + ": El Cuidador ha decidido matarme.");
		
		this.vivo = false;
			        
		return true;
	}

	private boolean tamaNoPuedeDestruirseEstaRealizandoOtraAccion() {
		
		System.out.println(nombreTama + " NO puede ser destruido, está " + this.estadoTama + ".");
		
		return false;
	}

	private boolean tamaYaEstabaMuerto() {
		
		System.out.println(nombreTama + " Ya está muerto.");
		 
		 return true;
	}

	private boolean comprobarTamaEstaEsperandoYVivo() {
		
		return this.estadoTama == EstadoTamagochi.ESPERANDO && this.vivo;
		
	}
	
	private void incrementarNivelSuciedadSiNoSeEstaLimpiando() {
		
		if (this.estadoTama != EstadoTamagochi.LIMPIANDOSE) {
			
			this.nivelSuciedad ++;
			
			System.out.println("\n"+nombreTama + " Suciedad actual: " + this.nivelSuciedad);
			
		}
	}

	private void comprobarTiempoDeVidaRestante(long tiempoInicioVida) {
		
		long tiempoActual = System.currentTimeMillis();
		
		if (tiempoActual - tiempoInicioVida >= TIEMPO_VIDA_MAX) {
			
			System.out.println("!!! AVISO " + nombreTama + ": Mi tiempo de vida de 5 minutos ha terminado. ¡HASTA LA VISTA BABY!");
			
		    this.vivo = false; 
		}
	}

	private void comprobarNivelDeSuciedadConLimiteEstablecido() {
		
		if (this.nivelSuciedad >= 5 && this.nivelSuciedad < 10) {
			
			System.out.println("!!Aviso " + nombreTama + ": Estoy empezando a estar MUY SUCIO (" + nivelSuciedad + ")");
			
		}else if (this.nivelSuciedad >= 10) {
			
			System.out.println("!!! ALERTA MÁXIMA " + nombreTama + ": He llegado a " + this.nivelSuciedad + " de suciedad. ¡HASTA LA VISTA BABY!");				
			
			this.vivo = false;
		}
	}
	
	private void asignarEstadoEsperando() {
		
		this.estadoTama = EstadoTamagochi.ESPERANDO;
		
	}
	
	private void mostrarMensajeEInterrumpirTamaYInterrumpirThread() {
		
		System.out.println(nombreTama + " ha sido interrumpido mientras esperaba.");
		
		Thread.currentThread().interrupt();
	}
	
	private void cambiarEstadoAMuertoYMostrarMensaje() {
		
		this.estadoTama = EstadoTamagochi.MUERTO; 
		
        System.out.println(nombreTama + " ha muerto. (Estado: " + this.estadoTama + ")");
        
	}	

	private void mensajeRespuestaCorrectaAJugar() {
		
		System.out.println("¡Correcto! " + nombreTama + " dice: ¡Bien hecho, Cuidador! Juego finalizado.");
	
	}

	private void mensajeRespuestaIncorrectaAJugar() {
	
		System.out.println("¡Incorrecto! " + nombreTama + " dice: ¡Sigo jugando! Generando nueva pregunta...");
	
	}
	
	private void cambiarEstadoAJugando() {
	
		this.estadoTama = EstadoTamagochi.JUGANDO;
	
	}

	private boolean comprobarRespuestaSuma(int respuesta) {
	
		boolean respuestaEsCorrecta = (respuesta == this.resultadoJuegoCorrecto);
		
		return respuestaEsCorrecta;
	
	}

	private void cambiarEstadoComer() {
	
		this.estadoTama = EstadoTamagochi.COMIENDO;
	
	}
	
	private boolean comprobarTamaEstaComiendo() {
		
		return this.estadoTama == EstadoTamagochi.COMIENDO;
	
	}

	private int generarTiempoTardaEnComer() {
	
		int tiempoComiendo = 1000 + rnd.nextInt(2000);
	
		return tiempoComiendo;
	
	}
	
	private void mostrarMensajeTamaNoPuedeLimpiarseEstaOcupado() {
	
		System.out.println(nombreTama + " no puede comer ahora, está " + this.estadoTama);
	
	}

	private void mostrarMensajeLimpiezaFinalizada() {
	
		System.out.println("<- " + nombreTama + " FINALIZA de limpiarse. Nivel de suciedad: " + this.nivelSuciedad);
	
	}

	private void mostrarMensajeEInterrumpirLimpieza() {
	
		System.out.println(nombreTama + " ¡Limpieza interrumpida! La suciedad sigue en " + this.nivelSuciedad);
		
		Thread.currentThread().interrupt();
	
	}

	private void cambiarEstadoALimpiarse() {
	
		this.estadoTama = EstadoTamagochi.LIMPIANDOSE;
	
	}
	
	private boolean comprobarTamaEstaLimpiandose() {
		
		return this.estadoTama == EstadoTamagochi.LIMPIANDOSE;
	
	}

	private void limpiarTamagochi() throws InterruptedException {
	
		Thread.sleep(TIEMPO_LIMPIEZA);
		
		nivelSuciedad = 0;
	
	}	

	private String mostrarMensajeTamaNoPuedeJugarEstaOcupado() {
	
		return nombreTama + " no puede jugar ahora, está " + this.estadoTama; 
	}

	private void resultadoSumaDeDosNumerosJugar(int primerNumeroSuma, int segundoNumeroSuma) {
	
		this.resultadoJuegoCorrecto = primerNumeroSuma + segundoNumeroSuma;
	
	}

	private int generarSegundoNumeroRandomSumaJugar(int primerNumeroSuma) {
	
		int segundoNumeroSuma = rnd.nextInt(NUMERO_MIN_GENERAR_RANDOM, NUMERO_MAX_GENERAR_RANDOM - primerNumeroSuma);
	
		return segundoNumeroSuma;
	
	}

	private int generarPrimerNumeroRandomSumaJugar() {
	
		int primerNumeroSuma = rnd.nextInt(NUMERO_MIN_GENERAR_RANDOM, NUMERO_MAX_GENERAR_RANDOM);
		
		return primerNumeroSuma;

	}

	private void mostrarMensajeTamaComienzaAJugar() {
	
		System.out.println("-> " + nombreTama + " COMIENZA a jugar. Pregunta generada.");

	}

	private void mostrarMensajeTamaHaSidoCreado() {
	
		System.out.println(nombreTama + " se ha creado y está " + estadoTama);
	
	}

	private void mostrarMensajeTamaTerminaComer(String comida) {
	
		System.out.println("<- " + nombreTama + " FINALIZA de comer " + comida);
	
	}

	private boolean comprobarTamaEstaJugando() {
	
		return this.estadoTama == EstadoTamagochi.JUGANDO;
	
	}
}
