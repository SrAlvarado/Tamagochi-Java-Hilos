package org.cuatrovientos.dam.psp.hilos.tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable{
	private String nombreTama;
	private EstadoTamagochi estadoTama;
	private int nivelSuciedad;
	private boolean vivo;
	private Random rnd;
	private int resultadoJuegoCorrecto;
	private static final long TIEMPO_ENSUCIAR = 20000;
	private static final long TIEMPO_VIDA_MAX = 300000;
	private static final long TIEMPO_LIMPIEZA = 5000;
	private static final int NUMERO_MIN_GENERAR = 1;
	private static final int NUMERO_MAX_GENERAR = 10;
	
	public Tamagochi(String nombreTama) {
		this.nombreTama = nombreTama;
		this.estadoTama = EstadoTamagochi.ESPERANDO;
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
    
    
	@Override
	public void run() {
		System.out.println(nombreTama + " ha despertado y está " + estadoTama);
		long tiempoInicioVida = System.currentTimeMillis();
		
		while(vivo) {
			try {
				Thread.sleep(TIEMPO_ENSUCIAR);
				
				if (this.estadoTama != EstadoTamagochi.LIMPIANDOSE) {
					this.nivelSuciedad ++;
					System.out.println("\n"+nombreTama + " Suciedad actual: " + this.nivelSuciedad);
				}
				
				if (this.nivelSuciedad >= 5 && this.nivelSuciedad < 10) {
					System.out.println("!!Aviso " + nombreTama + ": Estoy empezando a estar MUY SUCIO (" + nivelSuciedad + ")");
				}else if (this.nivelSuciedad >= 10) {
					System.out.println("!!! ALERTA MÁXIMA " + nombreTama + ": He llegado a " + this.nivelSuciedad + " de suciedad. ¡HASTA LA VISTA BABY!");				
					this.vivo = false;
				}
				
				long tiempoActual = System.currentTimeMillis();
				
				if (tiempoActual - tiempoInicioVida >= TIEMPO_VIDA_MAX) {
					System.out.println("!!! AVISO " + nombreTama + ": Mi tiempo de vida de 5 minutos ha terminado. ¡HASTA LA VISTA BABY!");
                    this.vivo = false; 
				}
				
				
			} catch (InterruptedException e) {
				System.out.println(nombreTama + " ha sido interrumpido mientras esperaba.");
                Thread.currentThread().interrupt();			
            }
		}
		
		this.estadoTama = EstadoTamagochi.MUERTO; 
        System.out.println(nombreTama + " ha muerto. (Estado: " + this.estadoTama + ")");
	}
	
	public synchronized void alimentarse(String comida) {
		if (this.estadoTama == EstadoTamagochi.ESPERANDO && this.vivo) {
			this.estadoTama = EstadoTamagochi.COMIENDO;
			System.out.println("-> " + nombreTama + " EMPIEZA de comer " + comida);
			
			int tiempoComiendo = 1000 + rnd.nextInt(2000);
			
			try {
				Thread.sleep(tiempoComiendo);
			}catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
			System.out.println("<- " + nombreTama + " FINALIZA de comer " + comida);
            this.estadoTama = EstadoTamagochi.ESPERANDO;
            
        } else {
            System.out.println(nombreTama + " no puede comer ahora, está " + this.estadoTama);
        }
		
	}
	
	public synchronized void limpiarse() {
		if (this.estadoTama == EstadoTamagochi.ESPERANDO && this.vivo) {
			this.estadoTama = EstadoTamagochi.LIMPIANDOSE;
			
			System.out.println("-> " + nombreTama + " EMPIEZA a limpiarse");
			
			try {
				Thread.sleep(TIEMPO_LIMPIEZA);
				nivelSuciedad = 0;

			} catch (InterruptedException e) {
				System.out.println(nombreTama + " ¡Limpieza interrumpida! La suciedad sigue en " + this.nivelSuciedad);
				Thread.currentThread().interrupt();
			}
			
			System.out.println("<- " + nombreTama + " FINALIZA de limpiarse. Nivel de suciedad: " + this.nivelSuciedad);
	        this.estadoTama = EstadoTamagochi.ESPERANDO; 
	        
		}else {
            System.out.println(nombreTama + " no puede comer ahora, está " + this.estadoTama);
		}
	}	
	
	
	public synchronized String generarPregunta() {
		if ((this.estadoTama != EstadoTamagochi.ESPERANDO && this.estadoTama != EstadoTamagochi.JUGANDO) || !this.vivo) {
	         return nombreTama + " no puede jugar ahora, está " + this.estadoTama;
	    }
	    
	    this.estadoTama = EstadoTamagochi.JUGANDO;
	    System.out.println("-> " + nombreTama + " COMIENZA a jugar. Pregunta generada.");
	    
	    int primerNumeroSuma = rnd.nextInt(NUMERO_MIN_GENERAR, NUMERO_MAX_GENERAR); 
	    int segundoNumeroSuma = rnd.nextInt(NUMERO_MIN_GENERAR, NUMERO_MAX_GENERAR - primerNumeroSuma); 
	    this.resultadoJuegoCorrecto = primerNumeroSuma + segundoNumeroSuma;
	    
	    return primerNumeroSuma + " + " + segundoNumeroSuma;
	}
	
	public synchronized boolean procesarRespuesta(int respuesta) {
	    if (comprobarTamaLibreYVivo()) {
	        System.out.println(nombreTama + " no estaba jugando, ignorando respuesta.");
	        return false;
	    }
	    
	    boolean esCorrecta = (respuesta == this.resultadoJuegoCorrecto);
	    
	    if (esCorrecta) {
	        System.out.println("¡Correcto! " + nombreTama + " dice: ¡Bien hecho, Cuidador! Juego finalizado.");
	        this.estadoTama = EstadoTamagochi.ESPERANDO;
	    } else {
	        System.out.println("¡Incorrecto! " + nombreTama + " dice: ¡Sigo jugando! Generando nueva pregunta...");
	    }
	    
	    return esCorrecta;
	}
	
	public synchronized boolean matar() {
		if (this.estadoTama == EstadoTamagochi.ESPERANDO && this.vivo) {
			System.out.println("!!! DESTRUCCIÓN INICIADA " + nombreTama + ": El Cuidador ha decidido matarme.");
	        
	        this.vivo = false;
	        	        
	        return true; 
	        
	    } else if (!this.vivo) {
	         System.out.println(nombreTama + " Ya está muerto.");
	         return true;
	    } else {
	        System.out.println(nombreTama + " NO puede ser destruido, está " + this.estadoTama + ".");
	        return false;
	    }
	}
	
	private boolean comprobarTamaLibreYVivo() {
		return this.estadoTama != EstadoTamagochi.JUGANDO || !this.vivo;
	}
}
