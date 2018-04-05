for $cursos in collection("/Cursos")//curso[aula=2]
let $curso := $cursos/nombre
let $profe := $cursos/profesor
let $dias := $cursos/dias
return concat($curso, ", impartido por ", $profe, ", que se imparten  ", $dias)