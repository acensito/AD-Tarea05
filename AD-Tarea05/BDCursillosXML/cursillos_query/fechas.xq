for $cursos in collection("Cursos")//curso
let $nombre := $cursos/nombre
let $inicio := $cursos/comienzo
let $fin := $cursos/fin
return concat($nombre, " con fecha de inicio el ", $inicio, " y fecha fin el ", $fin)