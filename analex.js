ejercicio= "programa id ; funcion idf ( char id , char id ) : bool if ( id >= id ) then retornar true ; else retornar false ; endif endf int id , id ; id = num ; id = num ; repeat id = id + num ; until idf ( id , id ) ; endfin"




ejercicio_separada = ejercicio.split(" ",9999)
ejercicio_separada=ejercicio_separada.reverse()
la_pila = ["$","prog"]


columnas = "id	num	(	)	litcad	litcar	+	-	*	/	<	>	<=	>=	!=	==	!	&&	||	true	false	if	while	repeat	else	then	do	endif	endwhile	programa	int	float	char	string	bool	,	procedimiento	funcion	endp	endf	endfin	idp	idf	retornar	$"
columnas_separada = columnas.split("	",9999)
filas = "prog dec sigid modulos proc fun tiporetorno list-arg siglist list-param sig-param sentencias sentencia sigif asigna L L' R R' E E' T T' F "   

filas_separada = filas.split(" ",9999)

gramatica = `																													programa id ; modulos dec sentencias endfin															
																														int id sigid ; dec	float id sigid ; dec	char id sigid ; dec	string id sigid ; dec	bool id sigid ; dec						ç				
																																			, id sigid									
ç																					ç	ç	ç							ç	ç	ç	ç	ç		proc	fun	ç						
																																				procedimiento idp ( list-arg ) sentencias endp								
																																					funcion idf ( list-arg ) : tiporetorno sentencias endf							
																														int	float	char	string	bool										
			ç																											int id siglist	float id siglist	char id siglist	string id siglist	bool id siglist										
			ç																																, list-arg									
L sig-param	L sig-param	L sig-param	ç	L sig-param	L sig-param														L sig-param	L sig-param																						L sig-param		
			ç																																 , L sig-param									
sentencia sentencias																					sentencia sentencias	sentencia sentencias	sentencia sentencias	ç			ç	ç										ç	ç	ç	sentencia sentencias		sentencia sentencias	ç
asigna ;																					if L then sentencias sigif endif	while L do sentencias endwhile 	repeat sentencias until L ;																		asigna ;		asigna ;	
																								else sentencias			ç													ç				ç
id = L																																									idp(list-param)		retornar L	
R L'	R L'	R L'		R L'	R L'											! L			R L'	R L'																						R L'		
			ç			ç	ç	ç	ç	ç	ç	ç	ç	ç	ç		&& R L'	|| R L'							ç	ç																		ç
E R'	E R'	E R'		E R'	E R'												ç	ç	E R'	E R'					ç	ç																E R'		ç
			ç			ç	ç	ç	ç	< E	> E	<= E	>= E	!= E	== E		ç	ç							ç	ç																		ç
T E'	T E'	T E'		T E'	T E'														T E'	T E'																						T E'		
			ç			+ T E'	- T E'	ç	ç	ç	ç	ç	ç	ç	ç		ç	ç							ç	ç																		ç
F T'	F T'	F T'		F T'	F T'														F T'	F T'																						F T'		
			ç			ç	ç	* F T'	/ F T'	ç	ç	ç	ç	ç	ç		ç	ç							ç	ç																		ç
id	num	( L )		litcad	litcar														true	false																						idf ( list-param )		
`


gramatica_separada = gramatica.split(`
`,9999)
gram = []
for (let i = 0; i < gramatica_separada.length; i++) {
    gram.push(gramatica_separada[i].split("	",9999))
}


repeticiones=0
do{
    var lexema_anal_izar=ejercicio_separada[ejercicio_separada.length-1]

    for (;;) {
        if(concuerda()){ 
            la_pila.pop()
            ejercicio_separada.pop()
            // console.log()
            // console.log("entro aca")
            break;
        }else if(lexema_anal_izar==";"){
            la_pila.pop()
            break;
        }
        lugar_columna = columnas_separada.indexOf(lexema_anal_izar)
        lugar_fila = filas_separada.indexOf(la_pila[la_pila.length-1])
        // console.log(la_pila)
        // console.log(lugar_columna)
        // console.log(lugar_fila)
        // console.log(lexema_anal_izar)
        // if(la_pila[la_pila.length-1]=="modulos" && lexema_anal_izar=="funcion"){
        //     la_pila.pop()
        //     la_pila.push("fun")
            
        // }else if(la_pila[la_pila.length-1]=="modulos" && lexema_anal_izar=="procedimiento"){
        //     la_pila.pop()
        //     la_pila.push("proc")
        // }else{
            accion = gram[lugar_fila][lugar_columna]
            console.log("---------------------------------")
            console.log("lexema: "+lexema_anal_izar)//la_pila[la_pila.length-1]
            console.log("lo ultimo de la pila: "+la_pila[la_pila.length-1])//la_pila[la_pila.length-1]
            console.log(lugar_columna+" gramatica "+ gram[2] )
            console.log(lugar_fila+" gramatica "+gram[2][15])
            console.log(lugar_columna+" "+ columnas_separada[0] )
            console.log(lugar_fila+" "+filas_separada[24])
            console.log(accion)
            console.log(la_pila)
            console.log("---------------------------------")
            accion_separado=accion.split(" ",9999)
            accion_separado=accion_separado.reverse()
            
                            
            // console.log("entro aqui")
            la_pila.pop()
            if(accion_separado!="ç")
            la_pila=la_pila.concat(accion_separado)
        // }
        
        
        repeticiones++
        if(repeticiones>100) break;
    }
    console.log(lexema_anal_izar)
    repeticiones++
}while(repeticiones<100)

// console.log(lugar_columna)
// console.log(lugar_fila)
// console.log(columnas_separada[29])
// console.log(accion)
// console.log(la_pila)

// console.log(accion_separado)


// la_pila=la_pila.concat(accion_separado)

// console.log(la_pila)
// console.log(lexema_anal_izar)
// console.log(ejercicio_separada)
// console.log(columnas_separada)
// console.log(filas_separada)
// // console.log(gramatica_separada)
// console.log(gram[0])







function concuerda(){
    if(la_pila[la_pila.length-1]==lexema_anal_izar) return true
    return false
}