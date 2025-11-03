package org.cuatrovientos.dam.psp.hilos.tamagochi;

import java.util.Random;

public class Tamagochi implements Runnable{
	private String nombreTama;
	private EstadoTamagochi estadoTama;
	private int nivelSuciedad;
	private boolean estadoVida;
	private Random rnd;
	
	
	public Tamagochi(String nombreTama) {
		this.nombreTama = nombreTama;
		this.estadoTama = EstadoTamagochi.ESPERANDO;
        this.nivelSuciedad = 0;
        this.estadoVida  = true;
        this.rnd = new Random();
	}
	
	@Override
	public void run() {
		
		
	}
	
	public void alimentarse(String comida) {
		if (this.estadoTama.equals(EstadoTamagochi.ESPERANDO) && this.estadoVida) {
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
	

	
}
