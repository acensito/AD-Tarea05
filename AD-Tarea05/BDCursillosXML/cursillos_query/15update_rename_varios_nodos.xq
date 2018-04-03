
(: renombre el nodo 'empresa/web' poniendolo en mayusculas:)
for $x in doc("Empresa.xml")//empresa/web
return
rename node $x
as upper-case(name($x))

