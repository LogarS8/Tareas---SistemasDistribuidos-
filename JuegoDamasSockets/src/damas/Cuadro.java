package damas;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;

public class Cuadro extends JButton{
       
    //Es la que esta en el cuadro.
    private Ficha ficha;
    //El color del cuadro.
    private Color color;
    //La posición del cuadro en el tablero.
    private final int x;
    private final int y;
    
    public Cuadro(Color color, int x, int y, Ficha ficha) {
        this.color = color;
        this.ficha = ficha;
        this.x = x;
        this.y = y;   
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        visualizar();
    }

    public int getXPos() {
        return x;
    }

    public int getYPos() {
        return y;
    }

    public void setFicha(Ficha ficha){
        this.ficha = ficha;  
        repaint();
    }

    public Ficha getFicha() {
        return ficha;
    }

    public boolean estaVacio() {
        return ficha == null;
    }

    public void visualizar() {
        setText("");
        setBackground(color);    
        repaint();
    }    

    private void dibujarFicha(Graphics g){
        if(!estaVacio()) {
            g.setColor(ficha.getColor());
            g.fillOval(10, 10, 55, 55);
            if (ficha.isEsReina()) {
                g.setColor(Color.WHITE);
                g.fillOval(20, 20, 30, 30);
            }
        }
    }

     @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs); //To change body of generated methods, choose Tools | Templates.
        dibujarFicha(grphcs);
    }

    @Override
    public String toString() {
        return "Cuadro{" + "ficha=" + ficha + ", color=" + color + ", x=" + x + ", y=" + y + '}';
    }
    
    
}
