package com.example.Drawings;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;  // Explicitly import from java.util
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.example.models.AFD;

public class Draw_AFD extends JPanel {
    private AFD automaton;
    private Map<String, Point> statePositions;
    private static final int STATE_RADIUS = 30;
    private static final int ARROW_SIZE = 10;
    
    public Draw_AFD(AFD automaton) {
        this.automaton = automaton;
        this.statePositions = new HashMap<>();
        calculateStatePositions();
    }

    private void calculateStatePositions() {
        java.util.List<String> states = automaton.getStates();
        int centerX = 400;
        int centerY = 300;
        int radius = 200;
        
        for (int i = 0; i < states.size(); i++) {
            double angle = 2 * Math.PI * i / states.size();
            int x = centerX + (int)(radius * Math.cos(angle));
            int y = centerY + (int)(radius * Math.sin(angle));
            statePositions.put(states.get(i), new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTransitions(g2d);
        
        drawStates(g2d);
    }

    private void drawStates(Graphics2D g2d) {
        for (String state : automaton.getStates()) {
            Point pos = statePositions.get(state);
            
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos.x - STATE_RADIUS, pos.y - STATE_RADIUS, 
                        STATE_RADIUS * 2, STATE_RADIUS * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - STATE_RADIUS, pos.y - STATE_RADIUS, 
                        STATE_RADIUS * 2, STATE_RADIUS * 2);

            if (automaton.getAcceptance_states().contains(state)) {
                g2d.drawOval(pos.x - STATE_RADIUS + 4, pos.y - STATE_RADIUS + 4, 
                            STATE_RADIUS * 2 - 8, STATE_RADIUS * 2 - 8);
            }

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(state);
            g2d.drawString(state, pos.x - textWidth/2, pos.y + fm.getAscent()/2);
        }
    }

    private void drawTransitions(Graphics2D g2d) {
        HashMap<String, java.util.List<String>> transitions = automaton.getTransitions_table();
        java.util.List<Character> alphabet = automaton.getAlphabet();
        
        for (String fromState : transitions.keySet()) {
            java.util.List<String> toStates = transitions.get(fromState);
            for (int i = 0; i < toStates.size(); i++) {
                String toState = toStates.get(i);
                String symbol = String.valueOf(alphabet.get(i));
                drawTransition(g2d, fromState, toState, symbol);
            }
        }
    }

    private void drawTransition(Graphics2D g2d, String fromState, String toState, String symbol) {
        Point start = statePositions.get(fromState);
        Point end = statePositions.get(toState);
        
        if (start == null || end == null) return;

        if (fromState.equals(toState)) {
            drawSelfTransition(g2d, start, symbol);
            return;
        }

        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        int startX = start.x + (int)(STATE_RADIUS * Math.cos(angle));
        int startY = start.y + (int)(STATE_RADIUS * Math.sin(angle));
        int endX = end.x - (int)(STATE_RADIUS * Math.cos(angle));
        int endY = end.y - (int)(STATE_RADIUS * Math.sin(angle));

        g2d.drawLine(startX, startY, endX, endY);
        drawArrowHead(g2d, endX, endY, angle);

        int midX = (startX + endX) / 2;
        int midY = (startY + endY) / 2;
        g2d.drawString(symbol, midX - 10, midY - 10);
    }

    private void drawSelfTransition(Graphics2D g2d, Point state, String symbol) {
        int x = state.x;
        int y = state.y - STATE_RADIUS;
        
        g2d.drawArc(x - 20, y - 20, 40, 40, 0, 360);
        
        g2d.drawString(symbol, x, y - 25);
    }

    private void drawArrowHead(Graphics2D g2d, int x, int y, double angle) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        xPoints[0] = x;
        yPoints[0] = y;
        xPoints[1] = x - ARROW_SIZE;
        yPoints[1] = y - ARROW_SIZE;
        xPoints[2] = x - ARROW_SIZE;
        yPoints[2] = y + ARROW_SIZE;
        
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        for (int i = 1; i < 3; i++) {
            int dx = xPoints[i] - x;
            int dy = yPoints[i] - y;
            xPoints[i] = x + (int)(dx * cos - dy * sin);
            yPoints[i] = y + (int)(dx * sin + dy * cos);
        }
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    public void displayAutomaton() {
        JFrame frame = new JFrame("AFD Visualization");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(this);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        HashMap<String, List<String>> transitions = new HashMap<>();
        transitions.put("q0", Arrays.asList("q0", "q1"));
        transitions.put("q1", Arrays.asList("q2", "q0"));
        transitions.put("q2", Arrays.asList("q1", "q2"));
        
        // Create the AFD
        AFD divisibilityAFD = new AFD(
            transitions,                     
            Arrays.asList("q0", "q1", "q2"),  
            Arrays.asList('0', '1'),         
            "q0",                           
            Arrays.asList("q0")             
        );
        
        // Create and display the visualization
        Draw_AFD drawer = new Draw_AFD(divisibilityAFD);
        drawer.displayAutomaton();
        }

}
