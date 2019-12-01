/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JOptionPane;
import net.htmlparser.jericho.*;

/**
 *
 * @author Darkestt
 */
public class Clasificador {
    Dato ConjuntoDatos[] = new Dato[447];       //que son los 447 archivos de datos q tenemos
    Registro Variables[] = new Registro[392];       //registro que va a contener las Variables mas la cantidad del archivo test
    boolean llenarDatos;
    String ClaseNoticia;
    
    public Clasificador() throws Exception{
        //IDEA: tener un arreglo q contenga la informacion de cada archivo del conjunto de datos
        //la informacion de cada archivo estara dentro de una clase(Dato) que contiene un arreglo con las repeticiones de cada palabra frecuente
        //y la clase de dicho 
        llenarDatos = false;
        ClaseNoticia = "";
        int Indice = 0;
        for(int i=0; i<447; i++){                         //inicializando Datos
            ConjuntoDatos[i] = new Dato();
        }
        
        BufferedReader br;
        try (FileReader fr = new FileReader ("data/VistaMinableRes.arff")){
            br = new BufferedReader(fr);
            
            String linea;
            while((linea = br.readLine()) != null){
               if(!(linea.equals(" ") || linea.equals("")))
               {     
                   if(llenarDatos){
                        //System.out.println("linea: "+linea);
                        ConjuntoDatos[Indice].Info(linea);
                        Indice++;
                   }
                   if(linea.equals("@data")){
                       llenarDatos = true;
                   }                                      
               }
            }
        }
        br.close();
        
        //Una vez tengo los datos del conjunto de datos
        //creo un arreglo q va a contener solo las variables a comparar(en un registro q tendra el nombre de la palabra y 
        //                                                                las veces q se repiten en el archivo test)
        //cada palabra del nuevo archivo sera compara con este arreglo, de haber coincidencia se le suma contador            
        int indice = 0;                                             //indice con el cual me movere en el arreglo de registros

        for(int i=0; i<392; i++){                         //inicializando registros
            Variables[i] = new Registro();
        }
        
        try (FileReader fr = new FileReader ("data/palabras.txt")){
            br = new BufferedReader(fr);
            
            String linea;
            while((linea = br.readLine()) != null){
               if(!(linea.equals(" ") || linea.equals("")))
               {                       
                    //System.out.println("linea: "+linea); 
                    Variables[indice].Palabra = linea;
                    indice++;
                    //System.out.println(indice); 
               }
            }
            
            /*for(int i=0; i<392; i++){                         
                System.out.println("linea: "+Variables[i].Palabra); 
            }*/
        }
        br.close();
        
    }
    public void KNN() throws Exception{
        int SumaTotal=0, Resta=0;
        double Menor = 100.0;                    //variable para escoger al archivo mas parecido (el cual es el menor)
        int PosClaseEscogida = 0;                //la posicion en el arreglo del Dato q mas se parece al Test, para escoger la Clase
        double DistEuclidea[] = new double[447]; //arreglo con todos los resultados de los calculos de la distancia euclidea
        //calculando distancia euclidea
        for(int i=0; i<447; i++){
            for(int j=0; j<392; j++){                
                //ConjuntoDatos[i].CantVariables[j]
                //Variables[j].Cantidad
                Resta= ConjuntoDatos[i].CantVariables[j] - Variables[j].Cantidad;
                SumaTotal +=  Resta*Resta;
                
            }
            DistEuclidea[i] = Math.sqrt(SumaTotal);
            if(DistEuclidea[i] < Menor){
                Menor = DistEuclidea[i];
                PosClaseEscogida = i;
            }
            SumaTotal = 0;
        }
        /*for(int i=0; i<447; i++){
            System.out.println("i = "+i+". DistEuclidea = "+DistEuclidea[i]);
        }*/
        
        //System.out.println("Menor = "+Menor+". PosClaseEscogida = "+PosClaseEscogida);
        //System.out.println("Clase = "+ConjuntoDatos[PosClaseEscogida].Clase);
        //Definiendo la Clase
        ClaseNoticia = ConjuntoDatos[PosClaseEscogida].Clase;
    }
    public void ContarRepeticiones(String Entrada) throws Exception{
        
        int TamañoArchivo = 0, contador = 0;    //tamaño del archivo DESORDENADO y del arreglo lista palabras sin contar espacios
        
        Stemm_es Lematizador = new Stemm_es(); //creo objeto del lematizador
        
        //leo el archivo resultante del lematizador y le quito los espacios y saltos de linea
        BufferedReader br;
        FileWriter fw;
        PrintWriter pw;
        try (FileReader fr = new FileReader ("data/Salida Lematizador - "+Entrada+".txt")){
            br = new BufferedReader(fr);
            fw = new FileWriter("data/Desordenado - "+Entrada+".txt");
            pw = new PrintWriter(fw);
            String linea;
            while((linea = br.readLine()) != null){
               if(!(linea.equals(" ") || linea.equals("")))
               {                  
                   pw.println(linea);
                   TamañoArchivo++;
               }
            }
        }
        br.close();       
        fw.close();
        pw.close(); 
        
        //leo la lista de palabras resultantes para luego ordenarlas alfabeticamente
        String ListaPalabras[] = new String[TamañoArchivo];
        TamañoArchivo = 0;  //saber la cantidad de palabras del archivo ORDENADO
        try (FileReader fr = new FileReader ("data/Desordenado - "+Entrada+".txt")){
            br = new BufferedReader(fr);
            fw = new FileWriter("data/Ordenado - "+Entrada+".txt");
            pw = new PrintWriter(fw);
            String linea;
            while((linea = br.readLine()) != null){
               ListaPalabras[contador] = Lematizador.stemm(linea); //paso aqui de nuevo el lematizador por algunas inconsistencias en la corrida anterior
               //ListaPalabras[contador] = linea;
               contador++;             
            }
            //se ordena el arreglo alfabeticamente
            Arrays.sort(ListaPalabras);
            for(int i=0; i<contador; i++)
            {
               if(!( ListaPalabras[i].equals(" ") || ListaPalabras[i].equals("") || ListaPalabras[i].equals("﻿") || (ListaPalabras[i].length()<=3) ))
               {                  
                   pw.println(ListaPalabras[i].trim());
                   TamañoArchivo++;
                   //System.out.println("Palabra = ."+ListaPalabras[i].replace("\\p{Punct}|¿|\"|'|¡|‘|’|°", "").trim()+".");
               }                        
            } 
        }
        br.close();       
        fw.close();
        pw.close();
        
        try (FileReader fr = new FileReader ("data/Ordenado - "+Entrada+".txt")){
            br = new BufferedReader(fr);
            String linea;
            
            //cuento ocurrencias de las palabras del arreglo de las variables
            while((linea = br.readLine()) != null){
                for(int i=0; i<392; i++){                         
                    //System.out.println("linea: "+Variables[i].Palabra);
                    if(Variables[i].Palabra.equals(linea))              //si la palabra esta en la lista de variables aumento cantidad
                    {
                        Variables[i].Cantidad++;
                    }
                }                          
            }
            /*for(int i=0; i<392; i++){                         
                System.out.println("Palabra: "+Variables[i].Palabra+". Cantidad = "+Variables[i].Cantidad);
            }*/
            
        }
        br.close();                            
    }
    /**
     *
     * @param cad
     * @param stem
     * @return
     */
    public boolean DiccionarioPalabras(String cad, String stem){
        
        //System.out.println("Palabra: "+cad+" - Lema: "+stem);
        //********Preposiciones**********
        if(cad.equals("a")) {return false;}
        if(cad.equals("con")) {return false;}
        if(cad.equals("de")) {return false;}
        if(cad.equals("en")) {return false;}
        if(cad.equals("para")) {return false;}
        if(cad.equals("por")) {return false;}
        if(cad.equals("entre")) {return false;}
        if(cad.equals("ante")) {return false;}
        if(cad.equals("bajo")) {return false;}
        if(cad.equals("desde")) {return false;}
        if(cad.equals("segun")) {return false;}
        if(cad.equals("sin")) {return false;}
        if(cad.equals("sobre")) {return false;}
        if(cad.equals("acerca")) {return false;}
        if(cad.equals("durante")) {return false;}
        if(cad.equals("hacia")) {return false;}
        if(cad.equals("hasta")) {return false;}
        if(cad.equals("mediante")) {return false;}

        //***********Articulos*************
        if(stem.equals("el")) {return false;}
        if(cad.equals("la")) {return false;}
        if(cad.equals("lo")) {return false;}
        if(cad.equals("al")) {return false;}
        if(cad.equals("del")) {return false;}
        if(cad.equals("los")) {return false;}
        if(cad.equals("las")) {return false;}
        if(cad.equals("un")) {return false;}
        if(cad.equals("una")) {return false;}
        if(cad.equals("unas")) {return false;}
        if(cad.equals("unos")) {return false;}

        //***********Conjunciones*************
        if(cad.equals("y")) {return false;}
        if(cad.equals("e")) {return false;}
        if(cad.equals("o")) {return false;}
        if(cad.equals("ó")) {return false;}
        if(cad.equals("u")) {return false;}
        if(cad.equals("ni")) {return false;}
        if(cad.equals("pero")) {return false;}
        if(cad.equals("sino")) {return false;}
        if(cad.equals("asi")) {return false;}
        if(cad.equals("embargo")) {return false;}
        if(cad.equals("obstante")) {return false;}
        if(cad.equals("ya")) {return false;}
        if(cad.equals("porque")) {return false;}
        if(cad.equals("dado")) {return false;}
        if(cad.equals("luego")) {return false;}
        if(cad.equals("fin")) {return false;}
        if(cad.equals("aunque")) {return false;}
        if(cad.equals("mas")) {return false;}
        if(cad.equals("más")) {return false;}

        //Adverbios de afirmaci�n y negaci�n
        if(stem.equals("si")) {return false;}
        if(cad.equals("también")) {return false;}
        if(cad.equals("no")) {return false;}
        if(cad.equals("tampoco")) {return false;}

        //Pronombres
        if(cad.equals("ella")) {return false;}
        if(cad.equals("ello")) {return false;}
        if(cad.equals("ellos")) {return false;}
        if(cad.equals("ellas")) {return false;}	
        if(cad.equals("nos")) {return false;}
        if(cad.equals("le")) {return false;}
        if(cad.equals("les")) {return false;}
        if(stem.equals("nuestr")) {return false;}
        if(cad.equals("esto")) {return false;}
        if(stem.equals("estos")) {return false;}
        if(stem.equals("estas")) {return false;}
        if(cad.equals("ese")) {return false;}
        if(cad.equals("esa")) {return false;}
        if(cad.equals("eso")) {return false;}
        if(cad.equals("esos")) {return false;}
        if(cad.equals("esas")) {return false;}
        if(stem.equals("aquell")) {return false;}
        if(cad.equals("uno")) {return false;}
        if(stem.equals("algun")) {return false;}
        if(stem.equals("ningun")) {return false;}
        if(stem.equals("cualqui")) {return false;}
        if(stem.equals("que")) {return false;}
        if(stem.equals("quien")) {return false;}
        if(stem.equals("cual")) {return false;}
        if(stem.equals("cuant")) {return false;}
        if(stem.equals("com")) {return false;}
        if(stem.equals("cuand")) {return false;}		
        if(stem.equals("dond")) {return false;}
        if(stem.equals("cuy")) {return false;}

        //Otros
        if(cad.equals("etc")) {return false;}
        if(cad.equals("través")) {return false;}
        if(cad.equals("nosotros")) {return false;}
        if(cad.equals("me")) {return false;}
        if(cad.equals("se")) {return false;}
        if(cad.equals("sus")) {return false;}
        if(cad.equals("es")) {return false;}
        if(cad.equals("son")) {return false;}
        if(cad.equals("como")) {return false;}
        if(cad.equals("este")) {return false;}
        if(cad.equals("esta")) {return false;}
        if(cad.equals("están")) {return false;}
        if(cad.equals("cuándo")) {return false;}
        if(cad.equals("dónde")) {return false;}
        if(cad.equals("donde")) {return false;}
        if(cad.equals("dura")) {return false;}
        if(cad.equals("duro")) {return false;}
        if(cad.equals("su")) {return false;}
        if(cad.equals("algunos")) {return false;}
        if(cad.equals("ninguno")) {return false;}
        if(cad.equals("tan")) {return false;}
        if(cad.equals("menos")) {return false;}
        if(cad.equals("flaco")) {return false;}
        if(cad.equals("flaca")) {return false;}
        if(cad.equals("gorda")) {return false;}
        if(cad.equals("gordo")) {return false;}
        if(cad.equals("delgado")) {return false;}
        if(cad.equals("delgada")) {return false;}
        if(cad.equals("ser")) {return false;}
        if(cad.equals("fácil")) {return false;}
        if(cad.equals("difícil")) {return false;}
        if(cad.equals("feliz")) {return false;}
        if(cad.equals("lindo")) {return false;}
        if(cad.equals("linda")) {return false;}
        if(cad.equals("feo")) {return false;}
        if(cad.equals("fea")) {return false;}
        if(cad.equals("bien")) {return false;}
        if(cad.equals("mal")) {return false;}
        if(cad.equals("bbc")) {return false;}
 
        return true;
    }

    /**
     *
     * @param Entrada
     * @return
     * @throws Exception
     */
    public String Clasificar(String Entrada) throws Exception {
        //JOptionPane.showMessageDialog(null,"Archivo: "+Entrada);
        int Pos;
                
        if( !(
                Entrada.endsWith(".html") ||
                Entrada.endsWith(".htm") ||
                Entrada.endsWith(".shtml") ||
                Entrada.endsWith(".shtm") ||
                Entrada.endsWith(".xhtml") ||
                Entrada.endsWith(".hta") ||
                Entrada.endsWith(".php") ||
                Entrada.endsWith(".php3") ||
                Entrada.endsWith(".phtml") ||
                Entrada.endsWith(".xml") ||
                Entrada.endsWith(".xsml") ||
                Entrada.endsWith(".xsl") ||
                Entrada.endsWith(".xsd") ||
                Entrada.endsWith(".kml") ||
                Entrada.endsWith(".wsdl") 
             )
          )
        {
            JOptionPane.showMessageDialog(null,"El archivo introducido no pertecenece a ninguno de los formatos soportados.\nFormatos Doportados: "+
                    "\".html\", \".htm\", \".shtml\", \".shtm\", \".xhtml\", \".hta\", \".php\", \".php3\", \".phtml\", \".xml\", \".xsml\", \".xsl\","
                    + " \".xsd\", \".kml\", \".wsdl\".");
        }else{
            String EntradaArchivo = Entrada;
            //ubicacion del ultimo "\"
            Pos = Entrada.lastIndexOf("\\");
            //obteniendo solo el nombre del archivo
            Entrada = Entrada.substring(Pos+1);
            JOptionPane.showMessageDialog(null,"Nombre del Archivo: "+Entrada);
            
        //si el archivo ya esta en el proyecto no hace la copia y simplemente toma el q esta      
            File SalidaF = new File("data/"+Entrada);
            if(!SalidaF.exists()){
                //Si el archivo no existe dentro del proyecto se copia a la carpeta data
                BufferedReader br;
                FileWriter fw;
                PrintWriter pw;
                try (FileReader fr = new FileReader (EntradaArchivo)){
                    br = new BufferedReader(fr);
                    fw = new FileWriter("data/"+Entrada);
                    pw = new PrintWriter(fw);
                    String linea;
                    while((linea = br.readLine()) != null){
                       pw.println(linea);
                    }
                }
                br.close();       
                fw.close();
                pw.close();
            }
       //Todo lo referente al parser HTML
            String sourceUrlString = "data/"+Entrada;
            
            if(sourceUrlString.indexOf(':')==-1){
                sourceUrlString="file:"+sourceUrlString; 
            }
            Source source;
            source = new Source(new URL(sourceUrlString));
            String renderedText=source.getRenderer().toString();
            
            //Archivo Escritura - Archivo con la salida del parser
            PrintWriter SalidaParserPW;
            try (FileWriter SalidaParserFW = new FileWriter("data/Salida Parser - "+Entrada+".txt")) {
                SalidaParserPW = new PrintWriter(SalidaParserFW);
                SalidaParserPW.println(renderedText);
            }
            SalidaParserPW.close();

            //////////////////  LEMATIZADOR //////////////////////

            //pasando todo a minuscula
            renderedText = renderedText.toLowerCase();
            //limpiando de signos de puntuacion
            renderedText = renderedText.replaceAll("\\p{Punct}|¿|\"|'|¡|ï|½|”|­|–|³|’|‘|´|±|—|…|→|↑|←|•|“|اليوم|روسيا|ø|º|°|§|©|»|«|　| |www", "");
            //eliminando numeros
            renderedText = renderedText.replaceAll("[0-9]", "");
                          
            //System.out.println(renderedText);            
            String Palabra, Resto;

            Stemm_es Lematizador = new Stemm_es(); //creo objeto del lematizador

            //como archivo de escritura del lematizador sera para "añadir" al correr varias veces sobre un mismo programa
            //los resultads se añadirian al existente, por eso de existir lo eliminamos antes de ejecutar
            SalidaF = new File("data/Salida Lematizador - "+Entrada+".txt");
            if(SalidaF.exists()){
                SalidaF.delete();
            }

            //Archivo Escritura - Archivo con la salida del Lematizador
            PrintWriter SalidaLematizadorPW;
            try (FileWriter SalidaLematizadorFW = new FileWriter("data/Salida Lematizador - "+Entrada+".txt", true)) {
                SalidaLematizadorPW = new PrintWriter(SalidaLematizadorFW);
                //recorriendo el string que q retorna el parser
                Pos = renderedText.indexOf(" ");              
                if(Pos == 0){Pos++;}
                Palabra = renderedText.substring(0, Pos);
                Resto = renderedText.substring(Pos);
                while(Resto.indexOf(" ") != -1)
                {
                    if((Palabra.length() > 3)&&(Palabra.length() < 20))//LIMPIEZA DE PALABRAS INUTILES 2: verifico que cada palabra tenga mas de 3 caracteres antes de pasarla al lematizador,                                            
                    {                       //estoy seguro que se me escaparan algunas y que posiblemente me coma algunas q puedan ser utiles, pero es algo =P
        /*Lematizador*/ String Lema = Lematizador.stemm(Palabra);                   
                        //JOptionPane.showMessageDialog(null,"Lematizador - Entrada: "+Palabra+". Salida: "+Lema);
                        if(DiccionarioPalabras(Palabra, Lema)){
                            SalidaLematizadorPW.println(Lema.trim()); //para que el lematizador no imprima en lista, cambiar "println()" por "print()"                        
                        }
                    }else{
                        //SalidaLematizadorPW.println("");
                        //System.out.println(Palabra); //ver las palabras posiblemente inutiles que se descartan
                    }   
                    Pos = Resto.indexOf(" ");
                    if(Pos == 0){Pos++;}
                    Palabra = Resto.substring(0, Pos);
                    Resto = Resto.substring(Pos);
                }
            }
            SalidaLematizadorPW.close();

            //info de archivos generados
            //JOptionPane.showMessageDialog(null,"Se generaron los archivos: \n"+"Salida Parser - "+Entrada+".txt\n"+"Salida Lematizador - "+Entrada+".txt");

            //////////////////  LEMATIZADOR //////////////////////
            //idea: como el lematizador imprime en lista, 
            //1. limpio la lista de los espacios y salto de liena
            //2. ordeno la lista de palabras alfabeticamente
            //3. cuento las palabras
            ContarRepeticiones(Entrada);

            //una vez contadas las palabras repetidas y todo eso se realiza el K-vecino
            KNN();

            //al terminar con el archivo actual se empieza con el siguiente
            //se eliminan los archivos auxiliares
            File Archivo = new File("data/Salida Parser - "+Entrada+".txt");
            Archivo.delete();
            Archivo = new File("data/Salida Lematizador - "+Entrada+".txt");
            Archivo.delete();
            Archivo = new File("data/Desordenado - "+Entrada+".txt");
            Archivo.delete();
            Archivo = new File("data/Ordenado - "+Entrada+".txt");
            Archivo.delete();
            Archivo = new File("data/"+Entrada);
            Archivo.delete();
        }
        return ClaseNoticia;
    }
}
