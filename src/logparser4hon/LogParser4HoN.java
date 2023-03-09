package logparser4hon;

import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Slam
 */
public class LogParser4HoN {

    private static File fichero;
    private static JFileChooser selector;
    private static FileFilter filtro;
    static analizador scan;

    public static void main(String[] args) {
        System.out.println("Log parser para Heroes of Newert\n"
                + "Creado por Slam para SNET 2021");
        filtro = new FileNameExtensionFilter("Archivo log", "log");
        selector = new JFileChooser();
        selector.setFileFilter(filtro);        
        selector.setMultiSelectionEnabled(true);
        selector.showOpenDialog(null);
        //fichero = selector.getSelectedFile();                
        File[] files = selector.getCurrentDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("log") ? true : false;
            }
        });
        System.out.println("Partidas encontradas: " + files.length);
        for (File f : files) {
            fichero = f;
            if (fichero != null) {
                System.out.println("\nLog: " + fichero.getAbsolutePath());
                scan = new analizador();
                scan.CargarLog(fichero);
                scan.EscribirSalida();
            }
        }
    }

}
