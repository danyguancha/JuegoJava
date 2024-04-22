import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.*;

class Carrera extends JFrame {

    private JPanel panel;
    private Semaphore semaphore;
    private int finishLine;
    private int carWidth = 50; // Ancho deseado del carro
    private int carHeight = 30; // Alto deseado del carro
    private int numLaps; // Número de vueltas que deben dar los carros
    private String[][] finalPositions; // Matriz para almacenar las posiciones finales de los carros
    private int lapsCompleted; // Contador de vueltas completadas

    public Carrera() {
        super("Juego de carreras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        String[] nombresCarros = {"Rayo", "Centella", "Bólido", "Relámpago", "Tormenta","Carro extra"};
        // Solicitar número de vueltas al usuario
        String input = JOptionPane.showInputDialog(null, "Ingrese el número de vueltas que deben dar los carros:");
        numLaps = Integer.parseInt(input);

        panel = new JPanel();
        panel.setLayout(null);

        semaphore = new Semaphore(1);
        finishLine = 970;
        lapsCompleted = 0;
        finalPositions = new String[6][3]; // Matriz de 5x3 para almacenar las posiciones finales de los carros

        // Crear los carros
        Carro[] carros = new Carro[6];
        for (int i = 0; i < carros.length; i++) {
            carros[i] = new Carro(nombresCarros[i], i + 1);
            panel.add(carros[i].getPanel());
            carros[i].start();
        }

        add(panel);
        setVisible(true);
    }

    class Carro extends Thread {

        private JPanel panel;
        private ImageIcon carroIcon;
        private int x, y;
        private int carNumber;
        private String nombreCarro;
        private long tiempoInicio; 
        

        public Carro(String nombre, int carNumber) {
            this.nombreCarro = nombre;
            this.carNumber = carNumber;
            ImageIcon originalIcon = new ImageIcon("C:/Users/danyg/OneDrive/Documentos/NetBeansProjects/juejo/src/clases/carro.png"); // Ubicación de la imagen del carro
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(carWidth, carHeight, Image.SCALE_SMOOTH);
            carroIcon = new ImageIcon(scaledImage);
            panel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    carroIcon.paintIcon(this, g, 0, 0);
                }
            };
            panel.setSize(carWidth, carHeight);
            x = 0;
            y = 50 * carNumber;
            panel.setLocation(x, y);
            tiempoInicio = System.nanoTime(); // Guarda el tiempo de inicio en nanosegundos
        }

        public JPanel getPanel() {
            return panel;
        }

        public void run() {

            int conta = 0;
            while (x <= finishLine && lapsCompleted <= numLaps) {
                try {
                    semaphore.acquire();
                    int distance = (int) (Math.random() * 10) + 1; // Movimiento aleatorio entre 1 y 10
                    x += distance;
                    panel.setLocation(x, y);
                    semaphore.release();
                    Thread.sleep(10); // Pausa para simular movimiento

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (x >= finishLine) {
                    x = 0;
                    conta++;
                    if (conta == numLaps) {
                        finalPositions[carNumber - 1][0] = nombreCarro; 
                        finalPositions[carNumber - 1][1] = String.valueOf(conta);
                        long tiempoFinal = System.nanoTime();
                        long duracion = tiempoFinal - tiempoInicio; 
                        long milisegundos = TimeUnit.NANOSECONDS.toMillis(duracion); // Convierte a milisegundos
                        long segundos = milisegundos / 1000; // Convierte a segundos
                        milisegundos = milisegundos % 1000; // Obtiene los milisegundos restantes
                        finalPositions[carNumber - 1][2] = segundos + " segundos " + milisegundos + " milisegundos"; // Guarda el tiempo de llegada en segundos y milisegundos
                        
                        showFinalPositions();
                        break;
                    }
                }

            }
        }
    }

    private void showFinalPositions() {
        JFrame frame = new JFrame("Posiciones Finales");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear modelo de tabla con los datos finales
        String[] columnNames = {"Carro", "Vueltas Completadas", "Tiempo de Llegada"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (int i = 0; i < finalPositions.length; i++) {
            model.addRow(new Object[]{finalPositions[i][0], finalPositions[i][1], finalPositions[i][2]});
        }
        // Crear tabla con el modelo
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Carrera();
    }
}
