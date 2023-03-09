package logparser4hon;

/**
 *
 * @author Slam
 */
public class jugador {

    private int equipo;
    private int posicion;
    private String nick;
    private String ip;
    private int kills;
    private int deaths;
    private int asistencias;
    private int nivel_alcanzado;
    //private int experiencia;
    private int oro;
    private int juegos_ganados;
    private int juegos_perdidos;

    public jugador() {
        this.equipo = 0;
        this.posicion = 0;
        this.nick = " ";
        this.ip = " ";
        this.kills = 0;
        this.deaths = 0;
        this.asistencias = 0;
        this.nivel_alcanzado = 0;
        this.oro = 0;
        this.juegos_ganados = 0;
        this.juegos_perdidos = 0;
    }

    public jugador(int equipo, int posicion, String nick, String ip, int kills, int deaths, int asistencias, int nivel_alcanzado, int oro, int juegos_ganados, int juegos_perdidos) {
        this.equipo = equipo;
        this.posicion = posicion;
        this.nick = nick;
        this.ip = ip;
        this.kills = kills;
        this.deaths = deaths;
        this.asistencias = asistencias;
        this.nivel_alcanzado = nivel_alcanzado;
        this.oro = oro;
        this.juegos_ganados = juegos_ganados;
        this.juegos_perdidos = juegos_perdidos;
    }

    public int getEquipo() {
        return equipo;
    }

    public void setEquipo(int equipo) {
        this.equipo = equipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getKills() {
        return kills;
    }

    public void contarAsesinatos() {
        kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void contarMuertes() {
        deaths++;
    }

    public int getAsistencias() {
        return asistencias;
    }

    public void contarAsistencias() {
        asistencias++;
    }

    public int getNivel_alcanzado() {
        return nivel_alcanzado;
    }

    public void setNivel_alcanzado(int nivel_alcanzado) {
        this.nivel_alcanzado = nivel_alcanzado;
    }

    public int getOro() {
        if (oro < 0) {
            oro = 0;
        }
        return oro;
    }

    public void setOro(int oro) {
        this.oro = oro;
    }

    public int getJuegos_ganados() {
        return juegos_ganados;
    }

    public void setJuegos_ganados(int juegos_ganados) {
        this.juegos_ganados = juegos_ganados;
    }

    public int getJuegos_perdidos() {
        return juegos_perdidos;
    }

    public void setJuegos_perdidos(int juegos_perdidos) {
        this.juegos_perdidos = juegos_perdidos;
    }

}
