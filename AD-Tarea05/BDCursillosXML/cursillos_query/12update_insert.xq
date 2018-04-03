(:Inserta dos nodos :)
insert node
<empresa id="2">
		<nombre>Empresa Z</nombre>
		<cif>B32674129</cif>
		<direccion> C\ Las Marinas 25</direccion>
		<localidad>Granada</localidad>
		<provincia>Granada</provincia>
		<cpostal>18004</cpostal>
		<telefono>952245689</telefono> 
		<email>empresaz@dominioz.es</email>
		<web>www.dominioz.es</web>
	</empresa>

after doc("Empresa.xml")//empresa[1],

insert node
<empresa id="4">
		<nombre>Empresa Beta</nombre>
		<cif>B12374129</cif>
		<direccion> C\ Golondrina 45</direccion>
		<localidad>Granada</localidad>
		<provincia>Granada</provincia>
		<cpostal>18004</cpostal>
		<telefono>952123456</telefono> 
		<email>empresabeta@dominiobeta.es</email>
		<web>www.dominiobeta.es</web>
	</empresa>

as last into doc("Empresa.xml")

