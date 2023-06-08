package compilador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizador {
    String patron_id = "^[a-zA-Z_$][a-zA-Z0-9_$]*";
    String patron_num = "^[-+]?\\d+(\\.\\d+)?$";
    String patron_litcar = "^'([^']*)'$";
    String patron_litcad = "^\"([^\"]*)\"$";
    ArrayList<String> la_pila;

    String[] tablas = new String[2];
    String tablaLexico = "";
    // String tablas[0] = "";

    String ejercicio = "programa identificadorson ; funcion identificadorsonf ( char identificadorson , char identificadorson ) : bool if ( identificadorson >= identificadorson ) then retornar true ; else retornar false ; endif endf int identificadorson , identificadorson ; identificadorson = 123 ; identificadorson = 123 ; repeat identificadorson = identificadorson + 123 ; until idf ( identificadorson , id ) ; endfin";
    String[] ejercicio_separado;

    String palabras_reservadas = "( ) + - * / < > <= >= != == ! && || true false if while repeat else then do endif endwhile programa int float char string bool , procedimiento funcion endfin retornar ; : = endf until $";
    String[] palabras_reservadas_separada = palabras_reservadas.split(" ");

    String columnas = "id	num	(	)	litcad	litcar	+	-	*	/	<	>	<=	>=	!=	==	!	&&	||	true	false	if	while	repeat	else	then	do	endif	endwhile	programa	int	float	char	string	bool	,	procedimiento	funcion	endp	endf	endfin	idp	idf	retornar	$";
    String[] columnas_separada = columnas.split("	");

    String filas = "prog dec sigid modulos proc fun tiporetorno list-arg siglist list-param sig-param sentencias sentencia sigif asigna L L' R R' E E' T T' F ";
    String[] filas_separada = filas.split(" ");

    String gramatica = "																													programa id ; modulos dec sentencias endfin															\nç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç	int id sigid ; dec	 float id sigid ; dec	 char id sigid ; dec	 string id sigid ; dec	 bool id sigid ; dec	ç	ç	ç	ç	ç	ç	ç	ç	ç	ç\n																																			, id sigid									\nç																					ç	ç	ç							ç	ç	ç	ç	ç		proc	fun	ç						\n																																				procedimiento idp ( list-arg ) sentencias endp								\n																																					funcion idf ( list-arg ) : tiporetorno sentencias endf							\n																														int	float	char	string	bool										\n			ç																											int id siglist	float id siglist	char id siglist	string id siglist	bool id siglist										\n			ç																																, list-arg									\nL sig-param	L sig-param	L sig-param	ç	L sig-param	L sig-param														L sig-param	L sig-param																						L sig-param		\n			ç																																, L sig-param									\nsentencia sentencias																					sentencia sentencias	sentencia sentencias	sentencia sentencias	ç			ç	ç										ç	ç	ç	sentencia sentencias		sentencia sentencias	ç\nasigna ;																					if L then sentencias sigif endif	while L do sentencias endwhile 	repeat sentencias until L ;																		asigna ;		asigna ;	\n																								else sentencias			ç													ç				ç\nid = L																																									idp ( list-param )		retornar L	\nR L'	R L'	R L'		R L'	R L'											! L			R L'	R L'															ç							R L'		\n			ç			ç	ç	ç	ç	ç	ç	ç	ç	ç	ç		&& R L'	|| R L'							ç	ç									ç									ç\nE R'	E R'	E R'		E R'	E R'												ç	ç	E R'	E R'					ç	ç									ç							E R'		ç\n			ç			ç	ç	ç	ç	< E	> E	<= E	>= E	!= E	== E		ç	ç							ç	ç									ç									ç\nT E'	T E'	T E'		T E'	T E'														T E'	T E'															ç							T E'		\n			ç			+ T E'	- T E'	ç	ç	ç	ç	ç	ç	ç	ç		ç	ç							ç	ç									ç									ç\nF T'	F T'	F T'		F T'	F T'														F T'	F T'															ç							F T'		\n			ç			ç	ç	* F T'	/ F T'	ç	ç	ç	ç	ç	ç		ç	ç							ç	ç									ç									ç\nid	num	( L )		litcad	litcar														true	false															ç							idf ( list-param )		";

    String[] gramatica_separada = gramatica.split("\n");
    ArrayList<String[]> gram = new ArrayList<String[]>();

    Boolean ban_proc = false;
    Boolean ban_func = false;
    Boolean ban_until = false;

    Pattern pattern_id = Pattern.compile(patron_id);
    Pattern pattern_num = Pattern.compile(patron_num);
    Pattern pattern_litcar = Pattern.compile(patron_litcar);
    Pattern pattern_litcad = Pattern.compile(patron_litcad);
    String lexema_anal_izar;
    ArrayList<String> ejer;

    // filas26.add(i, rows1[i].split((char) 9 + "", 45));
    // String text = txt_limpiar.replaceAll("\\r\\n|\\r|\\n", "");
    int numero_linea;

    public Analizador(String texto) {
        numero_linea = 1;
        la_pila = new ArrayList<String>();
        la_pila.add("$");
        la_pila.add("prog");
        // texto = texto.replaceAll("\\r\\n|\\r|\\n", "");
        ejercicio_separado = texto.split(" ");
        ejercicio_separado = voltear(ejercicio_separado);
        System.out.println(ejercicio_separado.length);
        ejer = new ArrayList<>(Arrays.asList(ejercicio_separado));

        for (int i = 0; i < gramatica_separada.length; i++) {
            gram.add(gramatica_separada[i].split("	"));
        }
        // Sintactico();
    }

    String actual = "";
    String anterior = "";

    public String lexico(String palabra) {

        System.out.println("Palabra");
        System.out.println("---------------------------------");
        System.out.println("Palabra a  a nalizar:   " + palabra);

        if (indesof(palabras_reservadas_separada, palabra)) {
            if (palabra.equals("procedimiento"))
                ban_proc = true;
            if (palabra.equals("funcion"))
                ban_func = true;
            if (palabra.equals("until"))
                ban_until = true;
            System.out.println("Es una palabra reservada");
            if (!palabra.equals(anterior)) {
                tablas[1] += "<tr>";
                tablas[1] += "<td> la palabra a analizar es: " + palabra + " </td>";
                tablas[1] += "<td> el lexema es: \'" + palabra
                        + "\" se regresa igual al ser una palabra reservada o un caracter especial</td>";
                tablas[1] += "</tr>";
                anterior = palabra;
            }
            return palabra;
        } else {
            tablas[1] += "<tr>";
            tablas[1] += "<td> la palabra a analizar es: " + palabra + " </td>";
            if (pattern_id.matcher(palabra).find()) {
                if (ban_proc) {
                    ban_proc = false;
                    System.out.println("Es un identificador de procedimiento");
                    tablas[1] += "<td> el lexema es: \"idp\" es un identificador de procedimienti </td>";
                    tablas[1] += "</tr>";
                    return "idp";

                }
                if (ban_func || ban_until) {
                    ban_func = false;
                    ban_until = false;
                    tablas[1] += "<td> el lexema es: \"idf\" es un identificador de funcion </td>";
                    tablas[1] += "</tr>";
                    return "idf";

                }
                tablas[1] += "<td> el lexema es: \"id\" es un identificador </td>";
                tablas[1] += "</tr>";
                return "id";
            }
            if (pattern_num.matcher(palabra).find()) {
                System.out.println("Es un numero");
                tablas[1] += "<td> el lexema es: \"num\" es un numero </td>";
                tablas[1] += "</tr>";
                return "num";
            }
            if (pattern_litcar.matcher(palabra).find()) {
                System.out.println("Es un es un caracter");
                tablas[1] += "<td> el lexema es: \"litcar\" hace refencia a un caracter </td>";
                tablas[1] += "</tr>";
                return "litcar";
            }
            if (pattern_litcad.matcher(palabra).find()) {
                System.out.println("Es un es una cadena");
                tablas[1] += "<td> el lexema es: \"litcad\" hace refencia a una cadena de texto </td>";
                tablas[1] += "</tr>";
                return "litcad";

            }
        }
        // System.out.println("---------------------------------");
        tablas[1] += "<tr>";
        tablas[1] += "<td> error lexico ne la linea " + numero_linea + " la palabra \"" + palabra
                + "\" no es reconocida </td>";
        tablas[1] += "</tr>";
        return "error";

    }

    String palabra = "";

    public String[] Sintactico() {
        int repeticiones = 0;
        try {

            do {
                if (la_pila.get(la_pila.size() - 1).equals("$")) {
                    System.out.println("Finalizo correctamente la ejecucion el programa");
                    // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
                    break;
                }
                System.out.println("---------------------------------");
                System.out.println("Ejercicio");
                System.out.println("---------------------------------");
                System.out.println("Ejercicio:   " + ejer);
                lexema_anal_izar = lexico(ejer.get(ejer.size() - 1).trim());
                if (lexema_anal_izar.equals("error"))
                    return tablas;
                for (;;) {
                    palabra = ejer.get(ejer.size() - 1);
                    tablas[0] += "<tr>";
                    // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

                    System.out.println("---------------------------------");
                    System.out.println("Lexema");
                    System.out.println("---------------------------------");
                    System.out.println("Lexema a  a nalizar:   " + lexema_anal_izar);
                    System.out.println("---------------------------------");
                    System.out.println("La pila");
                    System.out.println("---------------------------------");
                    System.out.println(la_pila);

                    tablas[0] += "<td>" + lexema_anal_izar + "</td>";
                    tablas[0] += "<td>" + la_pila + "</td>";

                    if (concuerda()) {
                        System.out.println("---------------------------------");
                        System.out.println("Concuerda");
                        System.out.println("---------------------------------");
                        System.out.println(
                                "concuerda, se saca : \"" + (la_pila.get(la_pila.size() - 1)) + "\" de la pila");

                        tablas[0] += "<td>concuerda</td>";
                        if (lexema_anal_izar.equals(";"))
                            numero_linea++;
                        la_pila.remove(la_pila.size() - 1);
                        ejer.remove(ejer.size() - 1);
                        // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

                        break;
                    } else if (lexema_anal_izar.equals(";") || lexema_anal_izar.equals("until")) {

                        la_pila.remove(la_pila.size() - 1);
                        // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

                        break;
                    }

                    int lugar_columna = buscar(columnas_separada, lexema_anal_izar);
                    int lugar_fila = buscar(filas_separada, la_pila.get(la_pila.size() - 1));

                    String accion = gram.get(lugar_fila)[lugar_columna];
                    System.out.println("---------------------------------");
                    System.out.println("accion");
                    System.out.println("---------------------------------");
                    System.out.println("accion a incluir en la lista: " + accion);
                    tablas[0] += "<td>" + accion + "</td>";

                    String[] accion_separado = null;
                    try {
                        accion_separado = accion.split(" ");
                    } catch (Exception e) {
                        // TODO: handle exception
                        System.out.println("Algo salio mal");
                    }
                    accion_separado = voltear(accion_separado);

                    // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

                    la_pila.remove(la_pila.size() - 1);
                    if (!accion_separado[0].equals("ç")) {
                        agregar_LaPila(accion_separado);
                    }
                    repeticiones++;
                    if (repeticiones > 200)
                        break;

                    tablas[0] += "<td>" + palabra + "</td>";

                    tablas[0] += "</tr>";

                }

                if (la_pila.get(la_pila.size() - 1).equals("$")) {
                    System.out.println("Ya estuvo");
                    System.out.println(repeticiones);
                    // System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

                    break;
                }
                repeticiones++;
            } while (repeticiones < 200);
        } catch (Exception e) {
            // TODO: handle exception
            tablas[0] += "<tr>";
            tablas[0] += "<td> error en la linea " + numero_linea + " la palabra incorrecta es " + palabra
                    + " </td>";
            tablas[0] += "<td> se esperaba " + sacarEsperado(la_pila.get(la_pila.size() - 1)) + " </td>";
            tablas[0] += "</tr>";
        }
        return tablas;
    }

    public String[] voltear(String[] vectorAvoltear) {
        String[] temp = new String[vectorAvoltear.length];
        for (int i = vectorAvoltear.length - 1, j = 0; i >= 0; i--, j++) {
            temp[j] = vectorAvoltear[i];
        }

        return temp;
    }

    public String sacarEsperado(String cad) {
        String segunda_accion = "";
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
        System.out.println(cad);
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
        if (cad.equals(""))
            return "se esperaba nada";
        int itera = 0;
        int fila = -1;
        do {
            System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
            System.out.println(la_pila.get(la_pila.size() - 1));
            System.out.println(la_pila);

            fila = buscar(filas_separada, la_pila.get(la_pila.size() - 1));
            if (fila == -1)
                la_pila.remove(la_pila.size() - 1);
            System.out.println(fila);

            System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
            if (itera > 10)
                break;
            itera++;
        } while (fila != -1);
        segunda_accion += "[";
        for (int i = 0; i < columnas_separada.length - 1; i++) {
            System.out.println(cad);
            if (!gram.get(fila)[i].equals(""))
                segunda_accion += " " + columnas_separada[i] + ", ";
        }
        segunda_accion += "]";
        return segunda_accion;
    }

    public String separar(String cadena, String[] vector) {
        String[] temp = new String[vector.length];
        return null;
    }

    public void imprimir(String[] vectorAvoltear) {
        System.out.print("[");
        for (int i = 0; i < vectorAvoltear.length; i++) {
            System.out.print(vectorAvoltear[i] + " , ");
        }
        System.out.print("]");
    }

    public boolean indesof(String[] vec, String buscar) {
        for (int i = 0; i < vec.length; i++) {
            if (vec[i].equals(buscar))
                return true;
        }
        return false;
    }

    public int buscar(String[] vec, String buscar) {
        for (int i = 0; i < vec.length; i++) {
            if (vec[i].equals(buscar))
                return i;
        }
        // System.out.println("ERROR: " + buscar + " " + ((int) buscar.charAt(10)));
        return -1;
    }

    public boolean concuerda() {
        if (la_pila.get(la_pila.size() - 1).equals(lexema_anal_izar))
            return true;
        return false;
    }

    public void agregar_LaPila(String[] vec) {
        for (int i = 0; i < vec.length; i++) {
            la_pila.add(vec[i]);
        }
    }

    public static void main(String[] args) {
        String ejercicio = "programa identificadorson ; funcion identificadorsonf ( char identificadorson , char identificadorson ) : bool if ( identificadorson >= identificadorson ) then retornar true ; else retornar false ; endif endf int identificadorson , identificadorson ; identificadorson = 123 ; identificadorson = 123 ; repeat identificadorson = identificadorson + 123 ; until idf ( identificadorson , id ) ; endfin";
        Analizador obj = new Analizador(ejercicio);
        obj.Sintactico();

    }

}
