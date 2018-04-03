for $curso in collection("/Cursos")//curso
where $curso/precio/@cuota="mensual"
order by $curso/nombre
return $curso/nombre