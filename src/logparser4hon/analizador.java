package logparser4hon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Slam
 */
public class analizador {

    partida juego;
    //jugador[] players;
    //LinkedList<jugador> players;
    jugador[] players1;
    File log;
    private boolean AI;

    //LinkedList<jugador> partida = new LinkedList<>();
    //jugador[] equipo1 = new jugador[5];
    //jugador[] equipo2 = new jugador[5];
    /*
     La estructura de un log en HoN es la siguiente:
     INFO_DATE date:"2021/27/01" time:"00:14:43"
     INFO_SERVER name:"Unnamed Server"
     INFO_GAME name:"HoN Russian Local" version:"1.0.45b"
     INFO_MATCH name:"SoyDotTtA's Game" id:"4294967295"
     INFO_MAP name:"grimmscrossing" version:"0.0.0"
     INFO_SETTINGS
     
     A partir de este instante se inicia la captación de los datos de jugadores, 
     con la estructura:
     PLAYER_CONNECT player:0 name:"nick" address:"127.0.0.1"
     La elección de equipos tiene la siguiente estructura:
     PLAYER_TEAM_CHANGE player:0 team:1
     Si se encuentra la siguiente línea, se procede a eliminar el jugador creado
     PLAYER_DISCONNECT player:0
     Para detectar aumento de nivel:
     HERO_LEVEL time:33850 x:10386 y:6738 z:0 player:4 team:1 level:2
     Para detectar las asistencias, se busca las líneas:
     HERO_ASSIST time:19050 x:9823 y:7632 z:0 player:4 team:1 target:"Hero_Kraken" owner:3
     Para detectar las muertes:
     HERO_DEATH time:19050 x:9823 y:7632 z:0 player:3 team:2 attacker:"Hero_DoctorRepulsor" owner:0
     Para detectar los asesinatos:
     KILL time:19050 x:9823 y:7632 z:0 player:0 team:1 target:"Hero_Kraken" attacker:"Hero_DoctorRepulsor" owner:3 assists:4
     Para detectar aumento de experiencia:
     EXP_EARNED time:19050 x:10012 y:7235 z:0 player:4 team:1 experience:75.00 source:"Hero_Kraken" owner:3
     Para detectar aumento de oro:
     GOLD_EARNED time:19050 x:9823 y:7632 z:0 player:0 team:1 source:"Hero_Kraken" owner:3 gold:205
     Para detectar disminucion de oro:
     GOLD_LOST time:19050 x:9847 y:7731 z:0 player:3 team:2 source:"Hero_DoctorRepulsor" owner:0 gold:30
     Para determinar quien gana la partida:
     GAME_END time:2955300 winner:"2"
     */
    public analizador() {
        juego = new partida();
        //players = new LinkedList<jugador>();      
        players1 = juego.getJugadores();
        this.log = null;
        this.AI = false;
    }

    public void CargarLog(File log) {
        this.log = log;
        CaptarDatos();
    }

    public void CaptarDatos() {
        /*Aqui va la lógica del parser*/
        try {
            String[] split1;
            FileReader fr = new FileReader(log);
            Scanner scan = new Scanner(fr);
            while (scan.hasNext()) {
                String linea = scan.nextLine().trim();
                //System.out.println(linea);
                switch (detector(corrector(linea))) {
                    case 0://Fecha y Hora                                         
                        split1 = linea.split("\"");
                        FechaHora(split1[1], split1[3]);
                        System.out.println("fecha y hora    - OK");
                        break;
                    case 1://Servidor
                        //INFO_SERVER name:"Unnamed Server"
                        System.out.println("server  - OK");
                        break;
                    case 2://Juego y Versión       
                        System.out.println("version - OK");
                        break;
                    case 3://Partida e ID
                        //INFO_MATCH name:"SoyDotTtA's Game" id:"4294967295"
                        split1 = linea.split("\"");
                        ServerName(split1[1], split1[3]);
                        System.out.println("partida  - OK");
                        break;
                    case 4://Mapa
                        //INFO_MAP name:"grimmscrossing" version:"0.0.0"
                        split1 = linea.split("\"");
                        NombreMapa(split1[1], split1[3]);
                        System.out.println("mapa    - OK");
                        break;
                    case 5://Player conectado
                        //PLAYER_CONNECT player:0 name:"nick" address:"127.0.0.1" --- 4
                        //PLAYER_CONNECT time:115250 player:0 name:"ivanpro" address:"10.86.122.153" --- 5
                        linea = corrector(linea);

                        split1 = linea.split("\"");
                        //Esto sucede cuando la reconexion ocurre al transcurrir un tiempo de juego
                        if (split1.length > 4) {
                            String player = linea.split(" ")[1].substring(7);
                            ConformarEquipo(Integer.valueOf(player.trim()), split1[1], split1[3]);
                        } else {
                            String player = linea.split(" ")[1].substring(7);
                            ConformarEquipo(Integer.valueOf(player.trim()), split1[1], split1[3]);
                        }
                        System.out.println("Cantidad de Jugadores actualmente: " + juego.getCantPlayers());
                        break;
                    case 6://Player desconectado
                        //PLAYER_DISCONNECT player:0                                                                                                
                        String chg[] = linea.split(" : ");

                        //Esto sucede cuando la desconexion ocurre al transcurrir un tiempo de juego
                        if (chg.length > 2) {
                            String val = corrector(chg[2]);
                            EliminarJugador(Integer.valueOf(val));
                        } else {
                            EliminarJugador(Integer.valueOf(chg[1]));
                        }
                        //System.out.println("Cantidad de Jugadores actualmente: "+players.size());
                        System.out.println("Cantidad de Jugadores actualmente: " + juego.getCantPlayers());
                        break;
                    case 7://Player cambia de equipo
                        //PLAYER_TEAM_CHANGE player:0 team:1
                        //System.out.println(linea);
                        String chg1[] = linea.split(": ");
                        //System.out.println("Jugador " + players.get(Integer.valueOf(chg1[1].split("  ")[0])).getNick() + " en pos: " + Integer.valueOf(chg1[1].split("  ")[0]) + ", ahora en equipo: " + Integer.valueOf(chg1[2]));
                        //System.out.println("Jugador " + players1[Integer.valueOf(chg1[1].split("  ")[0])].getNick() + " en pos: " + Integer.valueOf(chg1[1].split("  ")[0]) + ", ahora en equipo: " + Integer.valueOf(chg1[2]));
                        AsignarEquipoJugador(Integer.valueOf(chg1[1].split("  ")[0]), Integer.valueOf(chg1[2]));
                        break;
                    case 8://Player sube nivel
                        //HERO_LEVEL time:33850 x:10386 y:6738 z:0 player:4 team:1 level:2                        
                        linea = linea.trim();
                        linea = linea.replaceAll(" ", "");
                        //linea = linea.replaceAll("\\s{2,}", " ");                        
                        String[] chg2 = linea.split(":");//5,6,7                          
                        //System.out.println(chg2[5].split(" ")[0]+","+ Integer.valueOf(chg2[6].split(" ")[0])+","+ Integer.valueOf(chg2[7].split("  ")[0]));
                        ContarNivel(Integer.valueOf(chg2[5].split(" ")[0]), Integer.valueOf(chg2[6].split(" ")[0]), Integer.valueOf(chg2[7].split("  ")[0]));
                        break;
                    case 9://Player gana oro
                        //GOLD_EARNED time:14150 x:10821 y:7279 z:0 player:5 team:2 source:"Gadget_Assist" owner:1 gold:165                                                
                        String[] chg3 = corrector(linea).split(":");//5,9
                        //System.out.println(chg3[5].split(" ")[0]+","+chg3[chg3.length-1]);
                        ContarOro(Integer.valueOf(chg3[5].split(" ")[0]), 0, Integer.valueOf(chg3[chg3.length - 1]));
                        break;
                    case 10://Player pierde oro
                        //GOLD_LOST time:2896550 x:6107 y:6009 z:0 player:4 team:1 source:"Hero_Kunas" owner:5 gold:540
                        linea = corrector(linea);
                        if (linea.contains("Hero")) {
                            String[] chg4 = linea.split(":");
                            DescontarOro(Integer.valueOf(chg4[5].split(" ")[0]), 0, Integer.valueOf(chg4[chg4.length - 1]));
                        }
                        break;
                    case 11://Player da un kill o un creep lo da
                        //KILL time:57300 x:9894 y:5874 z:0 player:3 team:2 target:"Hero_Panda" attacker:"Hero_Kraken" owner:1 assists:2,5
                        linea = corrector(linea);
                        String[] chg5 = linea.split(":");
                        //Caso muerte por creep o torre
                        if (linea.contains("target:\"Hero") && !linea.contains("attacker:\"Hero")) {
                            //no hacer nada
                        } else if (linea.contains("target:\"Hero") && linea.contains("attacker:\"Hero")) {
                            //Muerte por Heroe (split 6,7)
                            //System.out.println(chg5[5].split(" ")[0]+","+chg5[6].split(" ")[0]);
                            ContarAsesinatos(Integer.valueOf(chg5[5].split(" ")[0]), Integer.valueOf(chg5[6].split(" ")[0]));
                        }
                        break;
                    case 12://Player da una asistencia
                        //HERO_ASSIST time:19050 x:9823 y:7632 z:0 player:4 team:1 target:"Hero_Kraken" owner:3
                        linea = corrector(linea);
                        if (linea.contains("target:\"Hero")) {
                            chg = linea.split(":");//6
                            //System.out.println("asist player: "+chg[6].split(" ")[0]);
                            ContarAsistencias(Integer.valueOf(chg[5].split(" ")[0]), 0);
                        }
                        break;
                    case 13://Player es asesinado (muere)
                        //HERO_DEATH time:57300 x:9894 y:5874 z:0 player:1 team:1 attacker:"Hero_Kraken" owner:3
                        linea = corrector(linea);
                        chg = linea.split(":");//5
                        //System.out.println("muere player: "+chg[5].split(" ")[0]);
                        ContarMuerte(Integer.valueOf(chg[5].split(" ")[0]), 0);
                        break;
                    case 14://Fin de partida
                        juego.Vencedor(Integer.valueOf(corrector(linea).split(":")[2].replace('"', ' ').trim()));
                        Ganandores(juego.obtenerVencedor() == 1 ? 1 : 2);
                        System.out.println("Equipos:\n"
                                + "Pos|Equipo | Nick    | Juegos ganados|Juegos Perdidos|Asesinatos|Asistencias|Muertes|Nivel|Oro|IP  ");
                        for (jugador j : players1) {
                            if (j != null) {
                                System.out.println(j.getPosicion() + "|    " + j.getEquipo() + "     " + j.getNick() + "  | " + j.getJuegos_ganados() + "| " + j.getJuegos_perdidos() + "  |   " + j.getKills() + "    |   " + j.getAsistencias() + "  |   " + j.getDeaths() + "   |   " + j.getNivel_alcanzado() + "   |   " + j.getOro() + "  |   " + j.getIp());
                            }
                        }
                        System.out.println("Victoria para " + (juego.obtenerVencedor() == 1 ? "Equipo 1" : "Equipo 2"));
                        detector("salir");
                        break;
                    case 15://Inicio partida                                                
                        System.out.println("----------------------------------------");
                        System.out.println("Fecha de inicio de la partida: " + juego.getFecha());
                        System.out.println("Hora de inicio de la partida: " + juego.getHora());
                        System.out.println("Nombre del mapa: " + juego.getMapa());
                        System.out.println("Nombre del servidor: " + juego.getNombre());
                        System.out.println("Versión del Servidor: " + juego.getVersion());
                        System.out.println("----------------------------------------");
                        System.out.println("Cantidad de jugadores: " + juego.getCantPlayers());
                        for (int i = 0; i < players1.length; i++) {
                            if (players1[i] != null) {
                                players1[i].setOro(600);
                            }
                        }
                        /*for (int i = 0; i < players.size(); i++) {
                         players.get(i).setOro(600);
                         }*/
                        break;
                    case 16://Player Random
                        break;
                    case 17://Compra de item descuenta del oro
                        //ITEM_PURCHASE time:0 x:5096 y:4916 z:-4 player:1 team:1 item:"Item_Marchers" cost:500
                        linea = corrector(linea);
                        chg = linea.split(":");//6,length-1
                        //System.out.println(chg[5].split(" ")[0]+","+chg[chg.length-1]);
                        DescontarOro(Integer.valueOf(chg[5].split(" ")[0]), 0, Integer.valueOf(chg[chg.length - 1]));
                        break;
                    case 18://salir
                        System.exit(0);
                        break;
                    default:
                        //"nada interesante aqui"
                        break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(analizador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String corrector(String linea) {
        linea = linea.trim();
        linea = linea.replaceAll(" ", "");
        return linea;
    }

    public int detector(String linea) {
        int salida = -1;

        if (linea.contains("��INFO_DATE")) {
            salida = 0;
        } else if (linea.contains("INFO_SERVER")) {
            salida = 1;
        } else if (linea.contains("INFO_GAME")) {
            salida = 2;
        } else if (linea.contains("INFO_MATCH")) {
            salida = 3;
        } else if (linea.contains("INFO_MAP")) {
            salida = 4;
        } else if (linea.contains("PLAYER_CONNECT")) {
            salida = 5;
        } else if (linea.contains("PLAYER_DISCONNECT")) {
            salida = 6;
        } else if (linea.contains("PLAYER_TEAM_CHANGE")) {
            salida = 7;
        } else if (linea.contains("HERO_LEVEL")) {
            salida = 8;
        } else if (linea.contains("GOLD_EARNED")) {
            salida = 9;
        } else if (linea.contains("GOLD_LOST")) {
            salida = 10;
        } else if (linea.contains("KILL")) {
            salida = 11;
        } else if (linea.contains("HERO_ASSIST")) {
            salida = 12;
        } else if (linea.contains("HERO_DEATH")) {
            salida = 13;
        } else if (linea.contains("GAME_END")) {
            salida = 14;
        } else if (linea.contains("GAME_START")) {
            salida = 15;
        } else if (linea.contains("PLAYER_RANDOM")) {
            AI = true;
            salida = 16;
        } else if (linea.contains("ITEM_PURCHASE")) {
            salida = 17;
        } else if (linea.equalsIgnoreCase("salir")) {
            return 18;
        }
        return salida;
    }

    public int interprete(String linea) {
        int salida = -1;
        return salida;
    }

    public void EscribirSalida() {
        /*Escribe un archivo en texto plano con la información de las partidas
         de forma que pueda ser ejecutado por un SGDB en lenguaje SQL*/
        try {
            File bd = new File("BD.sql");
            StringBuffer sb = new StringBuffer();
            if (!bd.exists()) {
                sb.append(GenerarTablasPartidaSQL());
                for (jugador j : players1) {
                    if (j != null) {
                        sb.append(GenerarConsultaInsertarNTablas(j) + "\n");
                    }
                }
            } else {
                //Cargo el contenido previo y lo añado a la nueva lectura                
                Scanner in = new Scanner(bd);
                while (in.hasNext()) {
                    sb.append(in.nextLine());
                }
                for (jugador j : players1) {
                    if (j != null) {
                        sb.append("\n" + GenerarConsultaInsertarNTablas(j));
                    }
                }
            }
            FileWriter fw = new FileWriter(bd);
            fw.append(sb.toString());
            fw.close();
        } catch (Exception e) {
            //capturo todas las excepcioens de una vez para su tratamiento
            //Ahora mismo no debe dar ninguna, sera para mejorar
            System.out.println("Excepción detectada: " + e.getMessage());
        }
    }

    public void GameVersionName(String name) {
        //do nothing
    }

    public void NombreMapa(String name, String version) {
        juego.setMapa(name);
        juego.setVersion(version);
    }

    public void NombrePartida(String name) {
        juego.setNombre(name);
    }

    public void ServerName(String name, String id) {
        //do nothing        
    }

    public void FechaHora(String fecha, String hora) {
        //System.out.println(fecha + "," + hora);
        juego.setFecha(fecha);
        juego.setHora(hora);
    }

    public void DescontarOro(int pos, int equipo, int oro) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].setOro(players1[pos].getOro() - oro);
                if (players1[pos].getOro() < 0) {
                    players1[pos].setOro(0);
                }
            }
        }
        //players.get(pos).setOro(players.get(pos).getOro() - oro);
    }

    public void ContarOro(int pos, int equipo, int oro) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].setOro(players1[pos].getOro() + oro);
            }
        }
        //players.get(pos).setOro(players.get(pos).getOro() + oro);
    }

    public void ContarNivel(int pos, int equipo, int lvl) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].setNivel_alcanzado(lvl);
            }
        }
        //players.get(pos).setNivel_alcanzado(lvl);
    }

    public void ContarAsistencias(int pos, int equipo) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].contarAsistencias();
            }
        }
        //players.get(pos).contarAsistencias();
    }

    public void ContarAsesinatos(int pos, int equipo) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].contarAsesinatos();
            }
        }
        //players.get(pos).contarAsesinatos();

    }

    public void ContarMuerte(int pos, int equipo) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].contarMuertes();
            }
        }
        //players.get(pos).contarMuertes();
    }

    public void EliminarJugador(int pos) {
        if (pos >= 0 && pos <= players1.length - 1) {
            players1[pos] = null;
            juego.actualizarCantPlayers();
        }
        /*if (pos >= 0 && pos < players.size()) {
         players.remove(pos);
         }*/
    }

    public void AsignarEquipoJugador(int pos, int equipo) {
        if (pos >= 0 && pos <= players1.length - 1) {
            if (players1[pos] != null) {
                players1[pos].setEquipo(equipo);
            }
        }
        /*if (pos >= 0 && pos <= players.size()) {
         players.get(pos).setEquipo(equipo);
         }*/
    }

    public void ConformarEquipo(int pos, String name, String ip) {
        jugador player = new jugador();
        player.setPosicion(pos);
        player.setNick(name);
        player.setIp(ip);
        juego.addPlayer(player);
    }

    public void Ganandores(int equipo) {
        for (jugador j : players1) {
            if (j != null) {
                if (j.getEquipo() == equipo) {
                    j.setJuegos_ganados(j.getJuegos_ganados() + 1);
                } else {
                    j.setJuegos_perdidos(j.getJuegos_perdidos() + 1);
                }
            }
        }
    }

    //+ "pos:" + j.getPosicion() + ","
    public String ResultadosPartidaJSON(jugador j) {
        String salida = "var jugador= {"
                + "nick:" + j.getNick() + ","
                + "ip:" + j.getIp() + ","
                + "team:" + j.getEquipo() + ","
                + "estadisticas: {"
                + "muertes:" + j.getDeaths() + ","
                + "asesinatos:" + j.getKills() + ","
                + "nivel:" + j.getNivel_alcanzado() + ","
                + "oro:" + j.getOro() + ","
                + "wins:" + j.getJuegos_ganados() + ","
                + "losts:" + j.getJuegos_perdidos() + ""
                + "}"
                + "}";
        return salida;
    }

    //+ j.getPosicion() + ",'"
    public String GenerarConsultaInsertarNTablas(jugador j) {
        String insertar = "INSERT INTO jugador VALUES("
                + j.getNick() + "',"
                + j.getEquipo() + ",'"
                + j.getIp() + ","
                + j.getDeaths() + ","
                + j.getKills() + ","
                + j.getAsistencias() + ","
                + j.getNivel_alcanzado() + ","
                + j.getOro() + ","
                + j.getJuegos_ganados() + ","
                + j.getJuegos_perdidos() + ",CURRENT_TIME,TRUE);";
        return insertar;
    }

    public String GenerarTablasPartidaSQL() {
        String crearTabla = "/*$Creado por Slam para SNET$*/\n\n"
                + "CREATE TABLE IF NOT EXISTS partida (\n"
                + "  'id_partida' int ,\n"
                + "  'nombre' text,\n"
                + "  'fecha' date,\n"
                + "  'cant_jugadores' int ,\n"
                + "  PRIMARY KEY('id_partida')\n"
                + ");\n"
                + "CREATE TABLE IF NOT EXISTS jugador (\n"
                + "  'id_jugador' int,\n"
                + "  'nick' text,\n"
                + "  'equipo' int,\n"
                + "  'ip' text,\n"
                + "  'muertes' int,\n"
                + "  'asesinatos' int,\n"
                + "  'asistencias' int,\n"
                + "  'nivel' int,\n"
                + "  'oro' int,\n"
                + "  'juegos_ganados' int,\n"
                + "  'juegos_perdidos' int,\n"
                + "  'ultima_conexion' date ,\n"
                + "  'estado' boolean,\n"
                + "  PRIMARY KEY ('id_jugador')  \n"
                + ");";
        return crearTabla;
    }

    public String ResultadosPartidaPlano() {
        String resultados = ""
                + "";
        return resultados;
    }
}
