/*
 INFO_DATE date:"2021/27/01" time:"00:14:43"
 INFO_SERVER name:"Unnamed Server"
 INFO_GAME name:"HoN Russian Local" version:"1.0.45b"
 INFO_MATCH name:"SoyDotTtA's Game" id:"4294967295"
 INFO_MAP name:"grimmscrossing" version:"0.0.0"
 */
package logparser4hon;

import java.util.LinkedList;

/**
 *
 * @author Slam
 */
public class partida {

    private String version;
    private String nombre;
    private String fecha;
    private String hora;
    private String mapa;
    private int vencedor;
    //private LinkedList<jugador> players = new LinkedList<>();
    private jugador[] players1;
    private int cantPlayers;

    public partida() {
        players1 = new jugador[10];
        cantPlayers = 0;
    }

    public partida(String version, String nombre, String fecha, String hora, String mapa) {
        this.version = version;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.mapa = mapa;
        players1 = new jugador[10];
        vencedor = 0;
        cantPlayers = 0;
    }    

    public void Vencedor(int winner) {
        this.vencedor = winner;
    }

    public int obtenerVencedor() {
        return vencedor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMapa() {
        return mapa;
    }

    public void setMapa(String mapa) {
        this.mapa = mapa;
    }

    public void addPlayer(jugador player) {
        if (cantPlayers < 10 && !existeJugador(player)) {
            players1[cantPlayers++] = player;
        }
        //players.add(player);
    }

    public boolean existeJugador(jugador player) {
        for (jugador j : players1) {
            if (j != null) {
                if (j.equals(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public jugador[] getJugadores() {
        return players1;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getCantPlayers() {
        return cantPlayers;//players.size();
    }
    
    public void actualizarCantPlayers(){
        int tcant=0;
        for(jugador j:players1){
            if(j!=null)
                tcant++;
        }
        cantPlayers = tcant;
    }
}
