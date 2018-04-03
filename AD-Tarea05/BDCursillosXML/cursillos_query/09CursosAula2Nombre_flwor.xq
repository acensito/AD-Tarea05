for $curso in collection("/Cursos")//curso
where $curso/aula=2
order by $curso/nombre
return $curso/nombre