package Proyecto;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class InventarioLibreria {

    static class Libro {
        String titulo;
        String autor;
        String ISBN;
        int stock;

        Libro(String titulo, String autor, String ISBN, int stock) {
            this.titulo = titulo;
            this.autor = autor;
            this.ISBN = ISBN;
            this.stock = stock;
        }
    }

    static ArrayList<Libro> inventario = new ArrayList<>();
    static Scanner entrada = new Scanner(System.in);
    static final String ARCHIVO = "inventario.txt";

    public static void main(String[] args) {
        cargarInventarioDesdeArchivo();
        login();
    }

    public static void login() {
        System.out.println("=== LOGIN ===");
        System.out.print("Usuario: ");
        String usuario = entrada.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = entrada.nextLine();

        if (usuario.equals("admin") && contrasena.equals("admin")) {
            menuAdmin();
        } else if (usuario.equals("invitado") && contrasena.equals("123")) {
            menuInvitado();
        } else {
            System.out.println("Credenciales incorrectas. Intente nuevamente.");
            login();
        }
    }

    public static void menuAdmin() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ ADMINISTRADOR ===");
            System.out.println("1. Registrar libro");
            System.out.println("2. Actualizar stock");
            System.out.println("3. Consultar inventario");
            System.out.println("4. Buscar libro");
            System.out.println("5. Generar reportes");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> registrarLibro();
                case 2 -> actualizarStock();
                case 3 -> consultarInventario();
                case 4 -> buscarLibro();
                case 5 -> generarReportes();
                case 6 -> {
                    guardarInventarioEnArchivo();
                    System.out.println("Guardando y saliendo...");
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 6);
    }

    public static void menuInvitado() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ INVITADO ===");
            System.out.println("1. Buscar libro");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> {
                    guardarInventarioEnArchivo();
                    System.out.println("Guardando y saliendo...");
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 2);
    }

    public static void registrarLibro() {
        System.out.println("\n--- REGISTRAR LIBRO ---");
        System.out.print("Título: ");
        String titulo = entrada.nextLine();
        System.out.print("Autor: ");
        String autor = entrada.nextLine();
        System.out.print("ISBN: ");
        String ISBN = entrada.nextLine();

        if (buscarPorISBN(ISBN) != null) {
            System.out.println("❌ El libro ya está registrado.");
            return;
        }

        System.out.print("Stock inicial: ");
        int stock = leerEntero();

        inventario.add(new Libro(titulo, autor, ISBN, stock));
        System.out.println("✅ Libro registrado exitosamente.");
    }

    public static void actualizarStock() {
        System.out.println("\n--- ACTUALIZAR STOCK ---");
        System.out.print("Ingrese el ISBN del libro: ");
        String ISBN = entrada.nextLine();

        Libro libro = buscarPorISBN(ISBN);
        if (libro == null) {
            System.out.println("❌ Libro no encontrado.");
            return;
        }

        System.out.print("Cantidad a ajustar (+ o -): ");
        int cantidad = leerEntero();
        int nuevoStock = libro.stock + cantidad;

        if (nuevoStock < 0) {
            System.out.println("❌ Stock insuficiente.");
        } else {
            libro.stock = nuevoStock;
            System.out.println("✅ Stock actualizado. Nuevo stock: " + libro.stock);
        }
    }

    public static void consultarInventario() {
        System.out.println("\n--- INVENTARIO COMPLETO ---");
        if (inventario.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }
        for (Libro libro : inventario) {
            mostrarLibro(libro);
        }
    }

    public static void buscarLibro() {
        System.out.println("\n--- BÚSQUEDA DE LIBRO ---");
        System.out.print("Buscar por título, autor o ISBN: ");
        String criterio = entrada.nextLine().toLowerCase();

        boolean encontrado = false;
        for (Libro libro : inventario) {
            if (libro.titulo.toLowerCase().contains(criterio) ||
                    libro.autor.toLowerCase().contains(criterio) ||
                    libro.ISBN.toLowerCase().contains(criterio)) {
                mostrarLibro(libro);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontraron libros con ese criterio.");
        }
    }

    public static void generarReportes() {
        System.out.println("\n====== 📊 REPORTES DEL INVENTARIO 📊 ======");

        if (inventario.isEmpty()) {
            System.out.println("No hay libros registrados en el inventario.");
            return;
        }

        // Ordenar los libros por stock descendente
        inventario.sort((a, b) -> Integer.compare(b.stock, a.stock));

        System.out.println("\n📚 Lista de libros ordenados por stock (mayor a menor):");
        for (Libro libro : inventario) {
            System.out.printf("• %-30s | Stock: %3d\n", libro.titulo, libro.stock);
        }

        // Mostrar libros con bajo stock
        System.out.println("\n⚠️ Libros con stock bajo (≤ 3):");
        boolean hayBajoStock = false;
        for (Libro libro : inventario) {
            if (libro.stock <= 3) {
                System.out.printf("  - %-30s | Stock: %3d\n", libro.titulo, libro.stock);
                hayBajoStock = true;
            }
        }
        if (!hayBajoStock) {
            System.out.println("  ✅ Todos los libros tienen buen stock.");
        }

        // Mostrar libro con mayor y menor stock
        Libro mayorStock = inventario.get(0);
        Libro menorStock = inventario.get(inventario.size() - 1);

        System.out.println("\n📈 Libro con más stock:");
        System.out.printf("  %s (Stock: %d)\n", mayorStock.titulo, mayorStock.stock);

        System.out.println("\n📉 Libro con menos stock:");
        System.out.printf("  %s (Stock: %d)\n", menorStock.titulo, menorStock.stock);

        System.out.println("===========================================\n");
    }

    public static Libro buscarPorISBN(String ISBN) {
        for (Libro libro : inventario) {
            if (libro.ISBN.equals(ISBN)) return libro;
        }
        return null;
    }

    public static void mostrarLibro(Libro libro) {
        System.out.println("---------------------------");
        System.out.println("Título: " + libro.titulo);
        System.out.println("Autor: " + libro.autor);
        System.out.println("ISBN: " + libro.ISBN);
        System.out.println("Stock: " + libro.stock);
    }

    public static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(entrada.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Intente con un número: ");
            }
        }
    }


    // Cargar inventario desde archivo

    public static void cargarInventarioDesdeArchivo() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            // Si no existe archivo, cargamos libros por defecto
            cargarLibrosPorDefecto();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4) {
                    String titulo = partes[0];
                    String autor = partes[1];
                    String ISBN = partes[2];
                    int stock = Integer.parseInt(partes[3]);
                    inventario.add(new Libro(titulo, autor, ISBN, stock));
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo de inventario: " + e.getMessage());
        }
    }

    // Libros por defecto

    public static void cargarLibrosPorDefecto() {
        inventario.add(new Libro("Cien Años de Soledad", "Gabriel García Márquez", "ISBN001", 5));
        inventario.add(new Libro("1984", "George Orwell", "ISBN002", 2));
        inventario.add(new Libro("El Principito", "Antoine de Saint-Exupéry", "ISBN003", 10));
    }

    // Guardar inventario en archivo

    public static void guardarInventarioEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Libro libro : inventario) {
                // formato: titulo|autor|ISBN|stock
                pw.println(libro.titulo + "|" + libro.autor + "|" + libro.ISBN + "|" + libro.stock);
            }
        } catch (IOException e) {
            System.out.println("Error guardando archivo de inventario: " + e.getMessage());
        }
    }
    
}
