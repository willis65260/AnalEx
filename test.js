
palabras_reservadas="( ) + - * / < > <= >= != == ! && || true false if while repeat else then do endif endwhile programa int float char string bool , procedimiento funcion endfin retornar ; : = $";
palabras_reservadas_separada = palabras_reservadas.split(" ", 9999);

pattern_id = /^[a-zA-Z_$][a-zA-Z0-9_$]*/
pattern_num = /^[-+]?\d+(\.\d+)?$/
patter_litcar = /^'([^']*)'$/
patter_litcad = /^"([^"]*)"$/
palabra = "int"

// if(pattern_num.exec("123"))
if(palabras_reservadas_separada.indexOf(palabra)!=-1)
    console.log("SIUUUU")


    // palabras_reservadas_separada.indexOf(palabra)!=-1