package DamasEnLinea;

import conexiones.Cliente;
import damas.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class TableroCliente extends javax.swing.JPanel implements ActionListener, Runnable {

    //Es la ventana que contiene el tablero.
    private final JugarCliente juego;
    //Son los cuadros del tablero.
    private final Cuadro[][] cuadros;
    //Son los jugadores que juegan en el tablero.
    private final Jugador[] jugadores;
    //Es el cuadro al que se le da click anteriormente.
    private Cuadro cuadroSeleccionado;
    //Indica que ficha se pueden comer dependiendo de la ficha seleccionada.
    private HashMap<Cuadro, Cuadro> fichaComer;
    //Es el turno de los jugadores.
    private int turno;
    //Indica si la partida se ha acabado.
    private boolean finPartida;
    //Sirve para llamar los movimientos al cliente
    private final Cliente cliente;

    /**
     * Se inicializa los atributos importantes.
     *
     * @param jugadores Son los jugadores agregados.
     * @param juego Es la ventana donde contiene el tablero.
     */
    public TableroCliente(Jugador[] jugadores, JugarCliente juego, Cliente cliente) {
        this.jugadores = jugadores;
        this.juego = juego;
        initComponents();
        finPartida = false;
        //Se indica que son 64 cuadros en todo el tablero.
        cuadros = new Cuadro[8][8];
        cuadroSeleccionado = null;
        fichaComer = new HashMap<>();
        turno = 0;
        //Se dibuja el tablero.
        dibujarTablero();
        juego.iniciarReloj();
        this.cliente = cliente;
        this.cliente.limpiarSalida();
        desactivarCuadros();
        new Thread(this).start();
    }

    /**
     * Se encarga de dibujar el tablero del juego en el panel con un arreglo de
     * cuadros que son botones.
     */
    private void dibujarTablero() {
        //Se indica que son 8 cuadros en y.
        for (int i = 0; i < 8; i++) {
            //Se indica que son 8 cuadros en x.
            for (int j = 0; j < 8; j++) {
                //Es la ficha que se va agregar al jugador y al cuadro.
                Ficha ficha = null;
                //Si la fila "y" es par, significa que el cuadro empieza con un cuadro rojo.
                if (i % 2 == 0) {
                    //Si la fila "x", cuando "y" es par, significa que empieza con un cuadro rojo.
                    if (j % 2 == 0) {
                        //Solo va agregar fichas si la fila "y" es menor a 3 y mayor 4.
                        if (i < 3 || i > 4) {
                            //Si es menor que 3 la fila, significa que es una ficha gris, si no, es naranja.
                            if (i < 3) {
                                ficha = new Ficha(j, i, Color.DARK_GRAY);
                                jugadores[0].agregarFicha(ficha);
                            } else {
                                ficha = new Ficha(j, i, Color.orange);
                                jugadores[1].agregarFicha(ficha);
                            }
                            //Agrega el cuadro segun las coordenadas y la ficha.
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        } else {
                            //La ficha es null, porque la fila esta entre 3 y 4.
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        }
                        //Si no es el cuadro rojo, significa que es el cuadro negro.    
                    } else {
                        //Agrega el cuadro negro con la ficha vacia.
                        cuadros[i][j] = new Cuadro(Color.black, j, i, ficha);
                        //Se le indica que no se puede clickear ya que no hay nada.
                        cuadros[i][j].setEnabled(false);
                    }
                    //Si la fila "y" es impar, significa que el cuadro empieza con un cuadro negro.
                } else {
                    if (j % 2 == 0) {
                        //Agrega el cuadro negro con la ficha vacia.
                        cuadros[i][j] = new Cuadro(Color.black, j, i, ficha);
                        //Se le indica que no se puede clickear ya que no hay nada.
                        cuadros[i][j].setEnabled(false);
                    } else {
                        //Solo va agregar fichas si la fila "y" es menor a 3 y mayor 4.
                        if (i < 3 || i > 4) {
                            if (i < 3) {
                                ficha = new Ficha(j, i, Color.DARK_GRAY);
                                jugadores[0].agregarFicha(ficha);
                            } else {
                                ficha = new Ficha(j, i, Color.orange);
                                jugadores[1].agregarFicha(ficha);
                            }
                            //Agrega el cuadro segun las coordenadas y la ficha.
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        } else {
                            //La ficha es null, porque la fila esta entre 3 y 4.
                            cuadros[i][j] = new Cuadro(Color.red, j, i, ficha);
                        }
                    }

                }
                //Se agrega el cuadro, la acción que tiene esta clase.
                cuadros[i][j].addActionListener(this);
                //Agrega al panel de esta clase.
                this.add(cuadros[i][j]);
                //Se visualiza el color de los cuadros con su ficha correspondiente.
                cuadros[i][j].visualizar();
            }
        }
        //Es para ver pruebas.
        System.out.println(verFichas());

    }

    /**
     * Activa los cuadros rojos despues del movimiento del contrincante.
     */
    private void activarCuadros() {
        //Se indica que son 8 cuadros en y.
        for (int i = 0; i < 8; i++) {
            //Se indica que son 8 cuadros en x.
            for (int j = 0; j < 8; j++) {
                //Si la fila "y" es par, significa que el cuadro empieza con un cuadro rojo.
                if (i % 2 == 0){
                     //Si la fila "x", cuando "y" es par, significa que empieza con un cuadro rojo.
                    if (j % 2 == 0) {
                        cuadros[i][j].setEnabled(true);
                    } //Si la fila "y" es impar, significa que el cuadro empieza con un cuadro negro.
                }
                else if(j % 2 != 0){
                    cuadros[i][j].setEnabled(true);
                }
            }
        }
    }

    /**
     * Desactiva los cuadros rojos despues de tu movimiento.
     */
    private void desactivarCuadros() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                cuadros[i][j].setEnabled(false);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(50, 50));
        setLayout(new java.awt.GridLayout(8, 8));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Es la acción que se realiza cuando se presiona un cuadro en la cual
     * consiste todo el juego, es el método más importante.
     *
     * @param e Es el botón osea el cuadro presionado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //Saca el cuadro clickeado con el mouse a una variable.
        Cuadro cuadro = (Cuadro) e.getSource();

        cliente.enviarDatosServidor(cuadro.getXPos() + " " + cuadro.getYPos());

        accionDespuesDePresionar(cuadro);

        cliente.limpiarSalida();
    }

    /**
     * Se ejecuta mientras el contrincante mueve las fichas. 
     */
    private void esperarTurno() {
        desactivarCuadros();
        while (true) {
            try {
                StringTokenizer separador = new StringTokenizer(cliente.leerDatosServidor());
                int x = Integer.parseInt(separador.nextToken());
                int y = Integer.parseInt(separador.nextToken());
                System.out.println(x + " " + y);
                accionDespuesDePresionar(cuadros[y][x]);
                if (turno == 1) {
                    activarCuadros();
                    break;
                }
            } catch (Exception e) {
                System.out.println("No hay dato todavía");
            }

        }

    }

    /**
     * Despues de que se presiona un cuadro realiza esto.
     *
     * @param cuadro El cuadro presionado.
     */
    public void accionDespuesDePresionar(Cuadro cuadro) {
        //Si el cuadro que se presiono contiene una ficha, quiere decir
        //que se desea saber si tiene movimiento.
        if (cuadro.getFicha() != null) {
            //Se le sacan las coordenadas de la ficha del cuadro.
            int x = cuadro.getFicha().getxPos();
            int y = cuadro.getFicha().getyPos();
            //Se indica que puede haber nuevas fichas para comer.
            fichaComer.clear();
            //Busca los cuadros amarrilos del anterior movimiento para quitarle el color.
            buscarCuadrosAmarillos();
            //Si la ficha es gris y el turno es el primero. 
            if (Color.DARK_GRAY.equals(cuadro.getFicha().getColor()) && turno == 0) {
                //Si la ficha es reina significa que se mueve diferente.
                if (cuadro.getFicha().isEsReina()) {
                    IndicarReina(x, y, cuadro);
                } else {
                    //Indica que la ficha se puede mover en diagonal hacia abajo.
                    indicarMovimiento(x, y, cuadro, 1, 1);
                    indicarMovimiento(x, y, cuadro, 1, -1);
                }
                //Si la ficha es naranja y el turno es el segundo. 
            } else if (Color.ORANGE.equals(cuadro.getFicha().getColor()) && turno == 1) {
                if (cuadro.getFicha().isEsReina()) {
                    //Si la ficha es reina significa que se mueve diferente.
                    IndicarReina(x, y, cuadro);
                } else {
                    //Indica que la ficha se puede mover en diagonal hacia arriba.
                    indicarMovimiento(x, y, cuadro, -1, 1);
                    indicarMovimiento(x, y, cuadro, -1, -1);
                }
            }
        }
        //Si el cuadro no contiene ficha, puede ser porque es un movimiento de ficha.
        moverFicha(cuadro);
    }

    /**
     * Indica los movimientos que puede tener la reina.
     *
     * @param x La coordenada en x.
     * @param y La coordenada en y.
     * @param cuadro Es el cuadro presionado.
     */
    private void IndicarReina(int x, int y, Cuadro cuadro) {
        //Se puede mover en cuatro direcciones diferentes.
        for (int i = 0; i < 4; i++) {
            IndicarReina(x, y, cuadro, (i < 2) ? 1 : -1, (i % 2 == 0) ? 1 : -1);
        }
    }

    /**
     * Indica los movimentos de la reina.
     *
     * @param x La coordenada en x.
     * @param y La coordenada en y.
     * @param cuadro Es el cuadro presionado.
     * @param direccionY Indica hacia donde se debe mover la ficha en y.
     * @param direccionX Indica hacia donde se debe mover la ficha en x.
     */
    private void IndicarReina(int x, int y, Cuadro cuadro, int direccionY, int direccionX) {
        int i;
        //Si ya no hay cuadros a donde se puede mover la ficha reina, se rompe el ciclo.
        for (i = 1; true; i++) {
            //Se multiplica por la dirección para ir por todos los cuadros disponibles.
            if (!indicarMovimiento(x, y, cuadro, direccionY * i, direccionX * i)) {
                break;
            }
        }

    }

    /**
     * Método que hace reina la ficha si es que haya llegado al final del
     * tablero dependiendo del color.
     *
     * @param cuadro Es el cuadro a donde mueve la ficha y pregunta si la
     * coordenada de la ficha es la indicada para hacerse reina.
     */
    private void hacerReina(Cuadro cuadro) {

        if (!cuadro.getFicha().isEsReina()) {
            if (cuadro.getFicha().getColor().equals(Color.ORANGE) && cuadro.getFicha().getyPos() == 0) {
                JOptionPane.showMessageDialog(this, "Se ha hecho reina tu ficha", "Felicidades", JOptionPane.INFORMATION_MESSAGE);
                cuadro.getFicha().hacerReina();
            } else if (cuadro.getFicha().getColor().equals(Color.DARK_GRAY) && cuadro.getFicha().getyPos() == 7) {
                JOptionPane.showMessageDialog(this, "Se ha hecho reina tu ficha", "Felicidades", JOptionPane.INFORMATION_MESSAGE);
                cuadro.getFicha().hacerReina();
            }
        }
    }

    /**
     * Mueve la ficha en el tablero y además indica si la ficha que se movio
     * comio o si se hizo reina
     *
     * @param cuadro Es el cuadro a donde se mueve la ficha.
     */
    private void moverFicha(Cuadro cuadro) {
        if (cuadro.getColor().equals(Color.yellow)) {
            //Se le pide las coordenadas al cuadro.
            int x = cuadro.getXPos();
            int y = cuadro.getYPos();
            //Se pregunta si es un movimiento para comer.
            comer(cuadro);

            //Se le indica que el nuevo cuadro presionado, le de la ficha del anterior cuadro seleccionado.
            cuadro.setFicha(cuadroSeleccionado.getFicha());
            //Se le indica las coordenadas a la ficha.
            cuadro.getFicha().setxPos(x);
            cuadro.getFicha().setyPos(y);
            //Se devuelve el color original al cuadro.
            cuadro.setColor(Color.red);
            //Se vacia el cuadro seleccionado anteriormente de la ficha.
            cuadroSeleccionado.setFicha(null);
            //Se busca todos los cuadros amarillos y se hacen rojos.
            buscarCuadrosAmarillos();
            //Se pregunta si la ficha que movio esta en el final del tablero.
            hacerReina(cuadro);
            //Se pregunta si ya ha ganado un jugador.
            ganador();
            //Cambia el turno.
            cambiarTurno();
            //Pruebas
            System.out.println(verFichas());
            if (turno == 0) {
                new Thread(this).start();
            }

        }
    }

    /**
     * Método de prueba para ver las fichas actuales de cada uno.
     *
     * @return La info de cada jugador sus fichas.
     */
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

    /**
     * Método que dice que se acaba la partida y menciona quien gana.
     */
    private void ganador() {
        if (finPartida) {
            //Se detiene el tiempo.
            juego.detenerTiempo();
            JOptionPane.showMessageDialog(this, "Ha ganado el jugador " + jugadores[turno].getNombre() + " con un total de tiempo: " + juego.devolverReloj());
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    //Todos los cuadros se deshabilitan.
                    cuadros[i][j].setEnabled(false);
                }
            }

        }
    }

    /**
     * Indica los movimiento del cualquier ficha y los marca con amarrilo.
     *
     * @param x La coordenada en x.
     * @param y La coordenada en y.
     * @param cuadro Es el cuadro presionado.
     * @param direccionY Indica hacia donde se debe mover la ficha en y.
     * @param direccionX Indica hacia donde se debe mover la ficha en x.
     */
    private boolean indicarMovimiento(int x, int y, Cuadro cuadro, int direccionY, int direccionX) {
        try {
            //Si el cuadro siguiente al presionado esta vacio significa que se puede mover.
            if (cuadros[y + direccionY][x + direccionX].estaVacio()) {
                cuadros[y + direccionY][x + direccionX].setColor(Color.YELLOW);
                cuadroSeleccionado = cuadro;
                return true;
                //Se necesita saber el valor del bando para saber cual se puede comer.
            } else if (!cuadros[y + direccionY][x + direccionX].getFicha().getColor().equals(cuadro.getFicha().getColor())) {
                indicarComer(cuadro, x, y, (direccionY < 0) ? direccionY - 1 : direccionY + 1, (direccionX < 0) ? direccionX - 1 : direccionX + 1, cuadros[y + direccionY][x + direccionX]);
                return false;
            }
        } catch (Exception err) {
        }
        return false;
    }

    /**
     * Indica donde una ficha puede comer.
     *
     * @param x La coordenada en x.
     * @param y La coordenada en y.
     * @param cuadro Es el cuadro presionado.
     * @param direccionY Indica hacia donde se debe mover la ficha en y.
     * @param direccionX Indica hacia donde se debe mover la ficha en x.
     * @param fichaCuadro
     */
    private void indicarComer(Cuadro cuadro, int x, int y, int direccionY, int direccionX, Cuadro fichaCuadro) {
        try {
            if (cuadros[y + direccionY][x + direccionX].estaVacio()) {
                fichaComer.clear();
                //Indica el cuadro donde si la ficha se mueve al cuadro amarillo se la come.
                fichaComer.put(cuadros[y + direccionY][x + direccionX], fichaCuadro);
                cuadros[y + direccionY][x + direccionX].setColor(Color.YELLOW);
                cuadroSeleccionado = cuadro;
            }
        } catch (Exception err) {
        }
    }

    /**
     * Busca si el movimiento fue para comer o no.
     *
     * @param cuadro Es el cuadro presionado.
     */
    private void comer(Cuadro cuadro) {
        //Si el mapa de fichas de comer contiene el cuadro que se presiono, indica que se come la ficha.
        if (fichaComer.containsKey(cuadro)) {
            quitarFichaContrincante(fichaComer.get(cuadro).getFicha());
            fichaComer.get(cuadro).setFicha(null);
        }
    }

    /**
     * Quita la ficha cuando come del contrincante.
     *
     * @param ficha Es la ficha que se debe comer del contrincante.
     */
    private void quitarFichaContrincante(Ficha ficha) {
        turno++;
        if (turno > 1) {
            turno = 0;
        }
        jugadores[turno].quitarFicha(ficha);
        //Si el jugador que se le quito la ficha ya no tiene, significa que se acabo la partida.
        if (jugadores[turno].getNumFichas() == 0) {
            finPartida = true;
        }
        //Cambia el turno original del jugador.
        cambiarTurno();
    }

    /**
     * Busca los cuadros amarillos de movimiento, esto sirve por si quieres
     * mover otra ficha.
     */
    private void buscarCuadrosAmarillos() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cuadros[i][j].getColor().equals(Color.YELLOW)) {
                    cuadros[i][j].setColor(Color.red);
                }
            }
        }
    }

    /**
     * Cambia el turno del jugador.
     */
    private void cambiarTurno() {
        turno++;
        if (turno > 1) {
            turno = 0;
        }
        juego.obtenerTurno();

    }

    /**
     * Devuelve el turno actual de los jugadores
     *
     * @return Es el turno actual de los jugadores
     */
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
