/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fase2;

import de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
/**
 *
 * @author Darkestt
 */
public class Fase2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //el estilo del look and feel
        try{
            UIManager.setLookAndFeel(new SyntheticaBlackMoonLookAndFeel());
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "Error Look and Feel", 
                    "Error:"+ ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
        
        Interfaz I = new Interfaz();
        I.setVisible(true);
        I.setLocationRelativeTo(null);
    }
}
