package org.cuatrovientos.dam.psp.hilos.tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable{
	private String nombreTama;
	private EstadoTamagochi estadoTama;
	private int nivelSuciedad;
	private boolean vivo;
	private Random rnd;
	private static final long TIEMPO_ENSUCIAR = 20000;
	private static final long TIEMPO_VIDA_MAX = 300000;
	
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
					System.out.println(nombreTama + " Suciedad actual: " + this.nivelSuciedad);
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
				Thread.sleep(TIEMPO_ENSUCIAR);
				nivelSuciedad = 0;

			} catch (InterruptedException e) {
				System.out.println(nombreTama + " ¡Limpieza interrumpida! La suciedad sigue en " + this.nivelSuciedad);
				Thread.currentThread().interrupt();
			}
			System.out.println("<- " + nombreTama + " FINALIZA de limpiarse. Nivel de suciedad: " + this.nivelSuciedad);
	        this.estadoTama = EstadoTamagochi.ESPERANDO; 
		}
	}
	
	
	

	
}
