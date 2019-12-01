/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fase2;

/**
 *
 * @author Darkestt
 */
public class Dato {
    String Clase;
    int CantVariables[] = new int[392]; //cantidad de variables en la vista minable
    
    public Dato (){
        Clase = "";
    }
    public void Info(String linea){
        int Pos, i=0;
        String Valor, Resto;         
        Pos = linea.indexOf(",");       
        Valor = linea.substring(0, Pos);
        Resto = linea.substring(Pos+1);
        while(Resto.indexOf(",") != -1)
        {
            CantVariables[i] = Integer.parseInt(Valor);
            i++;
            //System.out.println("Valor: "+Valor);
            Pos = Resto.indexOf(",");
            Valor = Resto.substring(0, Pos);
            Resto = Resto.substring(Pos+1);
            
        }
        CantVariables[i] = Integer.parseInt(Valor);
        Clase = Resto.replaceAll("'", "");
        /*for(i=0; i<392; i++){
            System.out.println("i = "+i+" val = "+CantVariables[i]+".");
        }
        System.out.println("Clase: "+Clase);*/
    }
}
