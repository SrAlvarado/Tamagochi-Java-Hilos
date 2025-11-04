package org.cuatrovientos.dam.psp.hilos.tamagochi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cuidador {
    
    private static List<Tamagochi> misTamagotchis = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
	public static final String COLOR_VERDE = "\u001B[32m";
	public static final String COLOR_PREDETERMINADO = "\u001B[0m";
	public static final String COLOR_CIAN = "\u001B[36m";
	public static final String COLOR_ROJO = "\u001B[31m";

    public static void main(String[] args) {
    			
        System.out.println("Soy el Cuidador. ¡Vamos a crear los Tamagotchis!");

        crearYLanzarTamagotchis(3);
        
        while (true) {
            
            mostrarEstadoTamagotchis();
            
            mostrarMenuEnConsola();
            
            try {
                int opcion = Integer.parseInt(scanner.nextLine());
                
                if (opcion == 5) {
                    matarATodosYSalir();
                    break; 
                }
                
                if (opcion >= 1 && opcion <= 4) {
                    Tamagochi tamaElegido = seleccionarTamagotchi();
                    if (tamaElegido != null) {
                        ejecutarAccion(opcion, tamaElegido);
                    }
                } else {
                    System.out.println(COLOR_ROJO + "Opción no válida. Inténtalo de nuevo." + COLOR_PREDETERMINADO);
                }

            } catch (NumberFormatException e) {
                System.out.println(COLOR_ROJO + "Por favor, introduce un número válido." + COLOR_PREDETERMINADO);
            }
            
            try {
                Thread.sleep(100); 
            } catch (InterruptedException ignored) {}
        }
        
        System.out.println("El Cuidador se despide. ¡Hasta pronto!");
    }

	private static void mostrarMenuEnConsola() {
		System.out.println(COLOR_CIAN + "\n--- Menú de Acciones ---");
		System.out.println("1. Alimentar Tamagotchi (comida)");
		System.out.println("2. Limpiar Tamagotchi");
		System.out.println("3. Jugar con Tamagotchi");
		System.out.println("4. Matar Tamagotchi (Si está Ocioso)");
		System.out.println("5. Salir del programa (Mata a todos los Tamagotchis)");
		System.out.print("Elige una acción (1-5): " + COLOR_PREDETERMINADO);
	}


    private static void crearYLanzarTamagotchis(int cantidad) {
        for (int i = 1; i <= cantidad; i++) {
            Tamagochi nuevoTama = new Tamagochi("Tama-" + i);
            misTamagotchis.add(nuevoTama);
            
            nuevoTama.setScannerDelCuidador(scanner);
            
            Thread hiloTama = new Thread(nuevoTama);
            hiloTama.start(); 
            System.out.println("\nTamagotchi Tama-" + i + " ha sido lanzado (Hilo: " + hiloTama.getName() + ")");
        }
    }


    private static void mostrarEstadoTamagotchis() {
        System.out.println("\n*** ESTADO ACTUAL DE LOS TAMAGOTCHIS ***");
        
        for (int i = 0; i < misTamagotchis.size(); i++) {
            Tamagochi tama = misTamagotchis.get(i);
            
            if (tama.isVivo() || tama.getEstado() == EstadoTamagochi.MUERTO) {
                System.out.printf("[" + (i + 1) + "] " + tama.getNombreTama() + " | Estado: " + tama.getEstado().toString() + " | Suciedad: " + tama.getSuciedad() + "/10 | Vivo: " + tama.isVivo() + "\n");
            }
        }
    }

    private static Tamagochi seleccionarTamagotchi() {
        System.out.print("Introduce el número del Tamagotchi a afectar: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (indice >= 0 && indice < misTamagotchis.size()) {
                Tamagochi tama = misTamagotchis.get(indice);
                if (!tama.isVivo()) {
                    System.out.println(tama.getNombreTama() + " ya está muerto y no puede realizar acciones.");
                    return null;
                }
                return tama;
            } else {
                System.out.println(COLOR_ROJO + "Número de Tamagotchi no válido." + COLOR_PREDETERMINADO);
            }
        } catch (NumberFormatException e) {
            System.out.println(COLOR_ROJO + "Entrada no válida." + COLOR_PREDETERMINADO);
        }
        return null;
    }

    private static void ejecutarAccion(int opcion, Tamagochi tama) {
    	switch (opcion) {
    		case 1:
    			tama.comprobarEstadoYCambiaraAlimentarse();
    			break;
    		case 2: // Limpiar
    			tama.comprobarEstadoyCambiarEstadoalimpiarse();
    			break;
    		case 3: // Jugar
    			jugarConTamagotchi(tama);
    			break;
    		case 4: // Matar
    			tama.matar();
    			break;
    	}
    }
    
    private static void jugarConTamagotchi(Tamagochi tama) {
    	
    	boolean sigueJugando = true;

    	while (sigueJugando) {

    		tama.comprobarEstadoYCambiarEstadoaJugando();

    		if (tama.getEstado() != EstadoTamagochi.JUGANDO) {
    			
				sigueJugando = false;
				
				continue;
				
    		}
   
    		try {
    			
    			Thread.sleep(50); 
    			
    		} catch (InterruptedException ignored) {}

    		if (tama.getEstado() == EstadoTamagochi.ESPERANDO) {
    			
    			sigueJugando = false;
    			
    		} else if (tama.getEstado() == EstadoTamagochi.JUGANDO) {

    			System.out.print("\n¿Quieres intentar responder de nuevo con " + tama.getNombreTama() + "? (s/n): ");
    			
    			String intento = scanner.nextLine().toLowerCase();
    			
    			if (!intento.equals("s")) {
    			
    				System.out.println(tama.getNombreTama() + " vuelve a ESPERANDO. [Nota: Esto lo debería hacer el Tamagotchi, saliendo del bucle por ahora].");
    				
    				sigueJugando = false;
    			}
    			
    		} else {
    			
    			sigueJugando = false;
    			
    		}
    	}
    }


    private static void matarATodosYSalir() {
        System.out.println("\nATENCIÓN: Iniciando destrucción de todos los Tamagotchis antes de salir.");
        for (Tamagochi tama : misTamagotchis) {
            if (tama.isVivo()) {
                tama.matar(); 
            }
        }
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException ignored) {}
        
        System.out.println("Finalizando programa...");
    }
}