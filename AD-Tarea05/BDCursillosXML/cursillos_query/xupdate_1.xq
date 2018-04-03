(: Sustituir el nombre de la empresa de id=1 por EmpresaAlfa:)
replace value of node
doc("Empresa.xml")//empresa[@id="1"]/nombre
with "EmpresaAlfa"