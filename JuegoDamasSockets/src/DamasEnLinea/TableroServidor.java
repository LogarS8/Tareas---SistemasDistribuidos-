package DamasEnLinea;

import conexiones.Servidor;
import damas.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class TableroServidor extends javax.swing.JPanel implements ActionListener,Runnable {

    private final JugarServidor juego;
    private final Cuadro[][] cuadros;
    private final Jugador[] jugadores;
    private Cuadro cuadroSeleccionado;
    private HashMap<Cuadro, Cuadro> fichaComer;
    private int turno;
    private boolean finPartida;
    private final Servidor servidor;
    
    public TableroServidor(Jugador[] jugadores, JugarServidor juego, Servidor servidor) {
        this.jugadores = jugadores;
        this.juego = juego;
        initComponents();
        finPartida = false;
        cuadros = new Cuadro[8][8];
        cuadroSeleccionado = null;
        fichaComer = new HashMap<>();
        turno = 0;
        dibujarTablero();
        juego.iniciarReloj();
        this.servidor = servidor;
        this.servidor.limpiarSalida();
    }

    private void dibujarTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Ficha ficha = null;
                if (i % 2 == 0) {
                    if (j % 2 == 0) {
                        if (i < 3 || i > 4) {
                            if (i < 3) {
                                ficha = new Ficha(j, i, Color.DARK_GRAY);
                                jugadores[0].agregarFicha(ficha);
                            } else {
                                ficha = new Ficha(j, i, Color.orange);
                                jugadores[1].agregarFicha(ficha);
                            }
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        } else {
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        }
                    } else {
                        cuadros[i][j] = new Cuadro(Color.black, j, i, ficha);
                        cuadros[i][j].setEnabled(false);
                    }
                } else {
                    if (j % 2 == 0) {
                        cuadros[i][j] = new Cuadro(Color.black, j, i, ficha);
                        cuadros[i][j].setEnabled(false);
                    } else {
                        if (i < 3 || i > 4) {
                            if (i < 3) {
                                ficha = new Ficha(j, i, Color.DARK_GRAY);
                                jugadores[0].agregarFicha(ficha);
                            } else {
                                ficha = new Ficha(j, i, Color.orange);
                                jugadores[1].agregarFicha(ficha);
                            }
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        } else {
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        }
                    }

                }
                cuadros[i][j].addActionListener(this);
                this.add(cuadros[i][j]);
                cuadros[i][j].visualizar();
            }
        }
        System.out.println(verFichas());

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(50, 50));
        setLayout(new java.awt.GridLayout(8, 8));
    }// </editor-fold>//GEN-END:initComponents

    private void activarCuadros() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i % 2 == 0){
                    if (j % 2 == 0) {
                        cuadros[i][j].setEnabled(true);
                    } 
                }
                else if(j % 2 != 0){
                    cuadros[i][j].setEnabled(true);
                }
            }
        }
    }

    private void desactivarCuadros() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cuadros[i][j].setEnabled(false);
            }
        }
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        Cuadro cuadro = (Cuadro) e.getSource();
                      
        servidor.enviarDatoCliente(cuadro.getXPos() +" "+ cuadro.getYPos());
        
        accionDespuesDePresionar(cuadro);

        servidor.limpiarSalida();
    }
    
    private void esperarTurno() {
        desactivarCuadros();
        while (true) {
            try {
                StringTokenizer separador = new StringTokenizer(servidor.leerDatosCliente());
                int x = Integer.parseInt(separador.nextToken());
                int y = Integer.parseInt(separador.nextToken());
                System.out.println(x + " " + y);
                accionDespuesDePresionar(cuadros[y][x]);
                if (turno == 0) {
                    activarCuadros();
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("No hay dato todavía");
            }

        }

    }

    public void accionDespuesDePresionar(Cuadro cuadro){
        if (cuadro.getFicha() != null) {
            int x = cuadro.getFicha().getxPos();
            int y = cuadro.getFicha().getyPos();
            fichaComer.clear();
            buscarCuadrosAmarillos();
            if (Color.DARK_GRAY.equals(cuadro.getFicha().getColor()) && turno == 0) {
                if (cuadro.getFicha().isEsReina()) {
                    IndicarReina(x, y, cuadro);
                } else {
                    indicarMovimiento(x, y, cuadro, 1, 1);
                    indicarMovimiento(x, y, cuadro, 1, -1);
                }
            } else if (Color.ORANGE.equals(cuadro.getFicha().getColor()) && turno == 1) {
                if (cuadro.getFicha().isEsReina()) {
                    IndicarReina(x, y, cuadro);
                } else {
                    indicarMovimiento(x, y, cuadro, -1, 1);
                    indicarMovimiento(x, y, cuadro, -1, -1);
                }
            }
        }
        moverFicha(cuadro);       
    }

    private void IndicarReina(int x, int y, Cuadro cuadro) {
        for (int i = 0; i < 4; i++) 
            IndicarReina(x, y, cuadro, (i < 2)?1:-1, (i % 2 == 0)?1:-1);
    }

    private void IndicarReina(int x, int y, Cuadro cuadro, int direccionY, int direccionX) {
        int i;
        for (i = 1; true; i++) {
            if (!indicarMovimiento(x, y, cuadro, direccionY * i, direccionX * i)) {
                break;
            }
        }

    }

    private void hacerReina(Cuadro cuadro) {
        
        if(!cuadro.getFicha().isEsReina()){
            if (cuadro.getFicha().getColor().equals(Color.ORANGE) && cuadro.getFicha().getyPos() == 0) {
                JOptionPane.showMessageDialog(this, "Se ha hecho reina tu ficha", "Felicidades", JOptionPane.INFORMATION_MESSAGE);
                cuadro.getFicha().hacerReina();
            } else if (cuadro.getFicha().getColor().equals(Color.DARK_GRAY) && cuadro.getFicha().getyPos() == 7) {
                JOptionPane.showMessageDialog(this, "Se ha hecho reina tu ficha", "Felicidades", JOptionPane.INFORMATION_MESSAGE);
                cuadro.getFicha().hacerReina();
            }
        }
    }

    private void moverFicha(Cuadro cuadro) {
        if (cuadro.getColor().equals(Color.yellow)) {
            int x = cuadro.getXPos();
            int y = cuadro.getYPos();
            comer(cuadro);
            
            cuadro.setFicha(cuadroSeleccionado.getFicha());
            cuadro.getFicha().setxPos(x);
            cuadro.getFicha().setyPos(y);
            cuadro.setColor(Color.red);
            cuadroSeleccionado.setFicha(null);
            buscarCuadrosAmarillos();
            hacerReina(cuadro);
            ganador();
            cambiarTurno();
            System.out.println(verFichas());
            if(turno == 1){
                new Thread(this).start();
            }
            
        }
    }

    private String verFichas() {
        String cad = "";
        for (Jugador jugador : jugadores) {
            cad += jugador.getNombre() + " " + jugador.getNumFichas() + "\n";
            for (Ficha ficha : jugador.getFichas()) {
                cad += ficha + "\n";
            }
        }
        return cad;
    }

    private void ganador() {       
        if (finPartida) {
            juego.detenerTiempo();
            JOptionPane.showMessageDialog(this, "Ha ganado el jugador " + jugadores[turno].getNombre() + " con un total de tiempo: " + juego.devolverReloj());
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    cuadros[i][j].setEnabled(false);
                }
            }
            
        }
    }

    private boolean indicarMovimiento(int x, int y, Cuadro cuadro, int direccionY, int direccionX) {
        try {
            if (cuadros[y + direccionY][x + direccionX].estaVacio()) {
                cuadros[y + direccionY][x + direccionX].setColor(Color.YELLOW);
                cuadroSeleccionado = cuadro;
                return true;
            } else if (!cuadros[y + direccionY][x + direccionX].getFicha().getColor().equals(cuadro.getFicha().getColor())) {
                indicarComer(cuadro, x, y, (direccionY < 0)?direccionY - 1:direccionY + 1, (direccionX < 0)?direccionX - 1: direccionX + 1 , cuadros[y + direccionY][x + direccionX]);
                return false;
            }
        } catch (Exception err) {
        }
        return false;
    }

    private void indicarComer(Cuadro cuadro, int x, int y, int direccionY, int direccionX, Cuadro fichaCuadro) {
        try {
            if (cuadros[y + direccionY][x + direccionX].estaVacio()) {    
                fichaComer = new HashMap<>();
                fichaComer.put(cuadros[y + direccionY][x + direccionX], fichaCuadro);
                cuadros[y + direccionY][x + direccionX].setColor(Color.YELLOW);
                cuadroSeleccionado = cuadro;
            }
        } catch (Exception err) {
        }
    }

    private void comer(Cuadro cuadro) {
        if (fichaComer.containsKey(cuadro)) {
            quitarFichaContrincante(fichaComer.get(cuadro).getFicha());
            fichaComer.get(cuadro).setFicha(null);
        }
    }

    private void quitarFichaContrincante(Ficha ficha) {
        turno++;
        if (turno > 1) {
            turno = 0;
        }
        jugadores[turno].quitarFicha(ficha);
        if (jugadores[turno].getNumFichas() == 0) {
            finPartida = true;
        }
        cambiarTurno();
    }

    private void buscarCuadrosAmarillos() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cuadros[i][j].getColor().equals(Color.YELLOW)) {
                    cuadros[i][j].setColor(Color.red);
                }
            }
        }
    }

    private void cambiarTurno() {
        turno++;
        if (turno > 1) {
            turno = 0;
        }
        juego.obtenerTurno();
    }

    public int getTurno() {
        return turno;
    }

    @Override
    public void run() {
        esperarTurno();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}