import java.util.*;

public class Main {

    public static void main(String[] args) {
        int filas = 3;
        int columnas = 4;

        String[][] barco = new String[filas][columnas];

        String[] roles = {"CAPITAN", "ARTILLERO", "INGENIERO", "MARINERO"};

        Map<String, List<String>> mapaRoles = new HashMap<>();

        int ocupados = 0;
        int capacidad = filas * columnas;
        Random random = new Random();

        System.out.println("▶ Ocupando el barco con tripulantes...");

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                String rol = roles[random.nextInt(roles.length)];
                barco[i][j] = rol;

                mapaRoles.putIfAbsent(rol, new ArrayList<>());
                mapaRoles.get(rol).add("Fila " + i + ", Columna " + j);

                System.out.println("Se colocó un " + rol + " en [" + i + "][" + j + "]");
                ocupados++;

                if (ocupados == capacidad) {
                    System.out.println("\n El barco ha alcanzado su capacidad máxima de " + capacidad + " tripulantes.");
                }
            }
        }

        Scanner sc = new Scanner(System.in);
        String rolBuscado;

        System.out.println("\n Puedes buscar roles de tripulantes. Escribe 'SALIR' para terminar.\n");

        while (true) {
            System.out.print("🔍 Ingrese el rol a buscar: ");
            rolBuscado = sc.nextLine().toUpperCase();

            if (rolBuscado.equals("SALIR")) {
                System.out.println("👋 Saliendo del programa.");
                break;
            }

            if (mapaRoles.containsKey(rolBuscado)) {
                System.out.println("\n📍 Ubicaciones de tripulantes con rol " + rolBuscado + ":");
                for (String ubicacion : mapaRoles.get(rolBuscado)) {
                    System.out.println(" - " + ubicacion);
                }
            } else {
                System.out.println("⚠ No se encontró ningún tripulante con el rol " + rolBuscado);
            }

            System.out.println();
        }

        sc.close();
    }
}
