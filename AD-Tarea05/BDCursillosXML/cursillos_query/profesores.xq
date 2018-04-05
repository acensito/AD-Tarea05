for $cursos in collection("/Cursos")//curso
let $curso := $cursos/nombre
let $profe := $cursos/profesor
let $precio := $cursos/precio
where $cursos/precio>300 and $cursos/precio[@cuota="anual"]
order by $cursos/profesor
return concat($curso, ", impartido por ", $profe, ", con un precio de  ", $precio, " euros")