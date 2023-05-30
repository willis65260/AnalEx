package compilador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizadores {
	String patron_id = "^[a-zA-Z_$][a-zA-Z0-9_$]*";
	String patron_num = "^[-+]?\\d+(\\.\\d+)?$";
	String patron_litcar = "^'([^']*)'$";
	String patron_litcad = "^\"([^\"]*)\"$";
	ArrayList<String> la_pila;

	String ejercicio = "programa identificadorson ; funcion identificadorsonf ( char identificadorson , char identificadorson ) : bool if ( identificadorson >= identificadorson ) then retornar true ; else retornar false ; endif endf int identificadorson , identificadorson ; identificadorson = 123 ; identificadorson = 123 ; repeat identificadorson = identificadorson + 123 ; until idf ( identificadorson , id ) ; endfin";
	String[] ejercicio_separado = ejercicio.split(" ");

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
	public Analizadores() {
		la_pila = new ArrayList<String>();
		la_pila.add("$");
		la_pila.add("prog");
		ejercicio_separado = voltear(ejercicio_separado);
		ejer = new ArrayList<>(Arrays.asList(ejercicio_separado));

		for (int i = 0; i < gramatica_separada.length; i++) {
			gram.add(gramatica_separada[i].split("	"));
		}
		Sintactico();
	}

	public String lexico(String palabra) {
		if (indesof(palabras_reservadas_separada, palabra)) {
			if (palabra.equals("procedimiento"))
				ban_proc = true;
			if (palabra.equals("funcion"))
				ban_func = true;
			if (palabra.equals("until"))
				ban_until = true;
			return palabra;
		} else {
			if (pattern_id.matcher(palabra).find()) {
				if (ban_proc) {
					ban_proc = false;
					return "idp";
				}
				if (ban_func || ban_until) {
					ban_func = false;
					ban_until = false;
					return "idf";
				}
				return "id";
			}
			if (pattern_num.matcher(palabra).find())
				return "num";
			if (pattern_litcar.matcher(palabra).find())
				return "litcar";
			if (pattern_litcad.matcher(palabra).find())
				return "litcad";
		}
		return null;
	}

	public String Sintactico() {
		int repeticiones = 0;
		do {
			lexema_anal_izar = lexico(ejer.get(ejer.size() - 1));
			// var lexema_anal_izar = ejercicio_separada[ejercicio_separada.length - 1];

			for (;;) {
				if (la_pila.get(la_pila.size() - 1).equals("$")) {
					System.out.println("fue igual");
					break;
				}

				if (concuerda()) {
					la_pila.remove(la_pila.size() - 1);
					ejer.remove(ejer.size() - 1);
					// ejercicio_separado.remove(ejercicio_separada.);
					// System.out.println("concuerda: " + lexema_anal_izar);
					// System.out.println("entro aca")
					break;
				} else if (lexema_anal_izar.equals(";") || lexema_anal_izar.equals("until")) {
					la_pila.remove(la_pila.size() - 1);
					break;
				}

				int lugar_columna = buscar(columnas_separada, lexema_anal_izar);
				int lugar_fila = buscar(filas_separada, la_pila.get(la_pila.size() - 1));

				// System.out.println(lugar_columna)
				// System.out.println(lugar_fila)
				// System.out.println(lexema_anal_izar)
				// System.out.println(la_pila[la_pila.length - 1])
				// if(la_pila[la_pila.length-1]=="modulos" && lexema_anal_izar=="funcion"){
				// la_pila.pop()
				// la_pila.push("fun")

				// }else if(la_pila[la_pila.length-1]=="modulos" &&
				// lexema_anal_izar=="procedimiento"){
				// la_pila.pop()
				// la_pila.push("proc")
				// }else{
				String accion = gram.get(lugar_fila)[lugar_columna];
				System.out.println("---------------------------------");
				System.out.println("lexema a anal: " + lexema_anal_izar);
				// //la_pila[la_pila.length-1]
				// System.out.println("lo ultimo de la pila: " + la_pila[la_pila.length - 1]);
				// //la_pila[la_pila.length-1]
				// System.out.println(lugar_columna + " gramatica " + gram[2]);
				// System.out.println(lugar_fila + " gramatica " + gram[2][15]);
				// System.out.println(lugar_columna + " " + columnas_separada[0]);
				// System.out.println(lugar_fila + " " + filas_separada[24]);
				System.out.println(accion);
				// System.out.println(la_pila);
				// System.out.println("Iteracion: " + repeticiones);
				// if (lexema_anal_izar == "repeat") {
				// System.out.println(
				// "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
				// );
				// System.out.println("Repeat: " + repeticiones);
				// System.out.println(
				// "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
				// );
				// }
				String[] accion_separado = null;
				try {
					accion_separado = accion.split(" ");
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Algo salio mal");
				}
				accion_separado = voltear(accion_separado);
				System.out.println(Arrays.asList(accion_separado));
				System.out.println(la_pila);

				System.out.println("---------------------------------");
				// System.out.println("entro aqui")
				la_pila.remove(la_pila.size() - 1);
				if (!accion_separado[0].equals("ç"))
					agregar_LaPila(accion_separado);
				// la_pila = la_pila.concat(accion_separado);
				// }

				repeticiones++;
				if (repeticiones > 200)
					break;
			}
			if (la_pila.get(la_pila.size() - 1).equals("$")) {
				// System.out.println("Ya estuvo");
				System.out.println("Ya estuvo");
				System.out.println(repeticiones);

				break;
			}
			// System.out.println(lexema_anal_izar)
			repeticiones++;
		} while (repeticiones < 200);

		return null;
	}

	public String[] voltear(String[] vectorAvoltear) {
		String[] temp = new String[vectorAvoltear.length];
		for (int i = vectorAvoltear.length - 1, j = 0; i >= 0; i--, j++) {
			temp[j] = vectorAvoltear[i];
		}

		return temp;
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
		System.out.println("ERROR: " + buscar + " " + ((int) buscar.charAt(10)));
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
		Analizadores obj = new Analizadores();

	}

}
