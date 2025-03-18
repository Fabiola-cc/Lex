package com.example.Drawings;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints; // Explicitly import from java.util
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.example.models.AFD;

/**
 * Clase que se encarga de dibujar un Autómata Finito Determinista (AFD)
 * Extiende JPanel para poder dibujar componentes gráficos
 */
public class Draw_AFD extends JPanel {
    // El autómata que se va a visualizar
    private AFD automaton;
    // Mapa que almacena las posiciones de cada estado en la visualización
    private Map<String, Point> statePositions;
    // Constantes para el tamaño de los estados y flechas
    private static final int STATE_RADIUS = 30;
    private static final int ARROW_SIZE = 10;

    /**
     * Constructor que inicializa el visualizador con un autómata
     * 
     * @param automaton El AFD que se va a visualizar
     */
    public Draw_AFD(AFD automaton) {
        this.automaton = automaton;
        this.statePositions = new HashMap<>();
        calculateStatePositions();
    }

    /**
     * Calcula las posiciones de los estados en un círculo
     * Los estados se distribuyen uniformemente alrededor de un círculo central
     */
    private void calculateStatePositions() {
        java.util.List<String> states = automaton.getStates();
        int centerX = 400; // Centro del círculo en X
        int centerY = 300; // Centro del círculo en Y
        int radius = 200; // Radio del círculo

        // Calcula la posición de cada estado usando trigonometría
        for (int i = 0; i < states.size(); i++) {
            double angle = 2 * Math.PI * i / states.size();
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            statePositions.put(states.get(i), new Point(x, y));
        }
    }

    /**
     * Sobrescribe el método paintComponent para dibujar el autómata
     * Se llama automáticamente cuando el componente necesita ser redibujado
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Activa el antialiasing para mejorar la calidad visual
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibuja primero las transiciones y luego los estados
        drawTransitions(g2d);

        drawStates(g2d);
    }

    /**
     * Dibuja los estados del autómata
     * Los estados de aceptación se dibujan con un círculo doble
     */
    private void drawStates(Graphics2D g2d) {
        for (String state : automaton.getStates()) {
            Point pos = statePositions.get(state);

            // Dibuja el círculo blanco del estado
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos.x - STATE_RADIUS, pos.y - STATE_RADIUS,
                    STATE_RADIUS * 2, STATE_RADIUS * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - STATE_RADIUS, pos.y - STATE_RADIUS,
                    STATE_RADIUS * 2, STATE_RADIUS * 2);

            // Si es un estado de aceptación, dibuja un segundo círculo
            if (automaton.getAcceptance_states().contains(state)) {
                g2d.drawOval(pos.x - STATE_RADIUS + 4, pos.y - STATE_RADIUS + 4,
                        STATE_RADIUS * 2 - 8, STATE_RADIUS * 2 - 8);
            }

            // Centra el texto del estado
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(state);
            g2d.drawString(state, pos.x - textWidth / 2, pos.y + fm.getAscent() / 2);
        }
    }

    /**
     * Dibuja todas las transiciones del autómata
     * Procesa la tabla de transiciones y dibuja cada una individualmente
     */
    private void drawTransitions(Graphics2D g2d) {
        HashMap<String, java.util.List<String>> transitions = automaton.getTransitions_table();
        java.util.List<String> alphabet = automaton.getAlphabet();

        // Dibuja cada transición con su símbolo correspondiente
        for (String fromState : transitions.keySet()) {
            java.util.List<String> toStates = transitions.get(fromState);
            for (int i = 0; i < toStates.size(); i++) {
                String toState = toStates.get(i);
                String symbol = String.valueOf(alphabet.get(i));
                drawTransition(g2d, fromState, toState, symbol);
            }
        }
    }

    /**
     * Dibuja una transición individual entre dos estados
     * 
     * @param fromState Estado de origen
     * @param toState   Estado destino
     * @param symbol    Símbolo de la transición
     */
    private void drawTransition(Graphics2D g2d, String fromState, String toState, String symbol) {
        Point start = statePositions.get(fromState);
        Point end = statePositions.get(toState);

        if (start == null || end == null)
            return;

        // Si es una transición al mismo estado, dibuja un bucle
        if (fromState.equals(toState)) {
            drawSelfTransition(g2d, start, symbol);
            return;
        }

        // Calcula los puntos de inicio y fin de la flecha
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        int startX = start.x + (int) (STATE_RADIUS * Math.cos(angle));
        int startY = start.y + (int) (STATE_RADIUS * Math.sin(angle));
        int endX = end.x - (int) (STATE_RADIUS * Math.cos(angle));
        int endY = end.y - (int) (STATE_RADIUS * Math.sin(angle));

        // Dibuja la línea y la punta de la flecha
        g2d.drawLine(startX, startY, endX, endY);
        drawArrowHead(g2d, endX, endY, angle);

        // Dibuja el símbolo de la transición en el punto medio
        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        g2d.drawString(symbol, midX - 10, midY - 10);
    }

    /**
     * Dibuja una transición que va de un estado a sí mismo
     * Se representa como un arco circular sobre el estado
     */
    private void drawSelfTransition(Graphics2D g2d, Point state, String symbol) {
        int x = state.x;
        int y = state.y - STATE_RADIUS;

        // Dibuja un arco circular
        g2d.drawArc(x - 20, y - 20, 40, 40, 0, 360);

        // Coloca el símbolo sobre el arco
        g2d.drawString(symbol, x, y - 25);
    }

    /**
     * Dibuja la punta de la flecha en una transición
     * 
     * @param x     Coordenada X del punto final
     * @param y     Coordenada Y del punto final
     * @param angle Ángulo de la flecha
     */
    private void drawArrowHead(Graphics2D g2d, int x, int y, double angle) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        // Define los puntos del triángulo que forma la punta de la flecha
        xPoints[0] = x;
        yPoints[0] = y;
        xPoints[1] = x - ARROW_SIZE;
        yPoints[1] = y - ARROW_SIZE;
        xPoints[2] = x - ARROW_SIZE;
        yPoints[2] = y + ARROW_SIZE;

        // Rota los puntos según el ángulo de la flecha
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        for (int i = 1; i < 3; i++) {
            int dx = xPoints[i] - x;
            int dy = yPoints[i] - y;
            xPoints[i] = x + (int) (dx * cos - dy * sin);
            yPoints[i] = y + (int) (dx * sin + dy * cos);
        }

        // Dibuja y rellena el triángulo
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Muestra el autómata en una ventana
     * Crea un JFrame y añade este panel a él
     */
    public void displayAutomaton() {
        JFrame frame = new JFrame("AFD Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Método main para pruebas
     * Crea un AFD de ejemplo y lo visualiza
     */
    public static void main(String[] args) {
        // Crea un AFD de ejemplo con tres estados
        HashMap<String, List<String>> transitions = new HashMap<>();
        transitions.put("q0", Arrays.asList("q0", "q1"));
        transitions.put("q1", Arrays.asList("q2", "q0"));
        transitions.put("q2", Arrays.asList("q1", "q2"));

        // Crea el AFD con sus propiedades
        AFD divisibilityAFD = new AFD(
                transitions,
                Arrays.asList("q0", "q1", "q2"), // Estados
                Arrays.asList("0", "1"), // Alfabeto
                new ArrayList<>(), // tokens
                "q0", // Estado inicial
                Arrays.asList("q0") // Estados de aceptación
        );

        // Crea y muestra la visualización
        Draw_AFD drawer = new Draw_AFD(divisibilityAFD);
        drawer.displayAutomaton();
    }
}
