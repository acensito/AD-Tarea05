for $profesores in collection("Profesores")//profesor
let $nombre := $profesores/nombre
let $dni := $profesores/dni
let $email := $profesores/email
order by $dni
return concat($nombre, " con DNI n�mero ", $dni, " y email ", $email)