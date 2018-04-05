package Ejercicio2;
//Para manejar ficheros y stream

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
//para manejar BD XML
import com.qizx.api.Library;
import com.qizx.api.LibraryManager;
import com.qizx.api.Configuration;
//para manejar excepciones
import com.qizx.api.QizxException;
import com.qizx.api.Message;
import com.qizx.api.CompilationException;
//para gestionar consultas XQuery
import com.qizx.api.Item;
import com.qizx.api.ItemSequence;
import com.qizx.api.Node;
import com.qizx.api.Expression;
import com.qizx.api.util.XMLSerializer;

/**
 * ****************************************************************************
 * clase para ejecutar scripts de consulta XQuery en una BD de XML
 *
 * @author IMCG
 */
public class Query {

    //ruta del Grupo de BD
    private static final String directorioGrupoRoot = "dbxml";
    //Library o base de datos
    private static final String bdNombre = "Cursillos";
    //"Tutorial";
    //ruta donde se almacenan los scripts con consultas XQuery
    private static final String directorioScriptsRoot = "BDCursillosXML/cursillos_query/";
    //nombres de los scripts con consultas XQuery - ficheros .xq
    private static final String[] scriptNombre = {"ocupacion.xq", "profesores.xq", "fechas.xq", "dni.xq"};

    public static void main(String[] args) throws IOException, QizxException {
        
        //variables para controlar el rango de resultados de la consulta que se
        //imprimen
        int min = 0;
        int max = Integer.MAX_VALUE;
        //fichero para recoger cada script XQuery de consulta
        File scriptFile;
        //objeto file 'directorioGrupo' apuntando a la ruta del Library Group
        File directorioGrupo = new File(directorioGrupoRoot);
        // Conexión o apertura del gestor del grupo
        LibraryManager bdManager = Configuration.openLibraryGroup(directorioGrupo);
        //Conexión a la BD
        Library bd = bdManager.openLibrary(bdNombre);

        try {
            //Para cada script con consulta XQuery
            for (int i = 0; i < scriptNombre.length; ++i) {
                //recoge la ruta del fichero de script con consulta XQuery
                scriptFile = new File(directorioScriptsRoot + scriptNombre[i]);
                //mensaje indicando el script XQuery que se ejecutará
                System.out.println("Ejecutando '" + scriptFile + "'...");

                //carga el contenido del script XQuery en una cadena
                String consultaXquery = cargaScript(scriptFile);
                //imprime la expresión de consulta XQuery
                System.out.println("---\n" + consultaXquery + "\n---");

                //compila la consulta, almacenado resultado en expr
                Expression expr = compileExpression(bd, consultaXquery);
                //evalúa la consulta para mostrar resultados en el rango [min, max]
                evaluarExpression(expr, min, max);
            }
        } finally {
            //cierra conexión con BD
            cerrar(bd, bdManager);
        }
    }

    //Método para compilar la consulta controlando errores
    private static Expression compileExpression(Library bd,
            String consultaXquery)
            throws IOException, QizxException {
        Expression expr;
        try {
            expr = bd.compileExpression(consultaXquery);
        } catch (CompilationException e) {
            //mensajes de error devueltos tras la compilación
            Message[] messages = e.getMessages();
            for (int i = 0; i < messages.length; ++i) {
                System.out.println(messages[i].toString());
            }
            throw e;
        }
//devuelve consulta compilada
        return expr;
    }

    //método que evalúa la consulta y muestra un rango de resultados entre min y max
    private static void evaluarExpression(Expression expr,
            int min, int max)
            throws QizxException {
        //evalúa la consulta compilada obteniendo un ItemSequence
        ItemSequence results = expr.evaluate();
        if (min > 0) {
            results.skip(min);
        }

        XMLSerializer serializer = new XMLSerializer();
        serializer.setIndent(2);

        int count = 0;
        while (results.moveToNextItem()) {
            Item result = results.getCurrentItem();
            //genera el número de orden para cada resultado de la consulta
            System.out.print("[" + (min + 1 + count) + "] ");
            //imprime uno de los resultados de la consulta
            mostrarResultado(serializer, result);
            System.out.println();

            ++count;
            //si ya se han imprimido un total de max resultados, no imprime más
            if (count >= max) {
                break;
            }
        }
        System.out.flush();
    }

    //Método para mostrar el resultado
    private static void mostrarResultado(XMLSerializer serializer,
            Item result)
            throws QizxException {
        //Si no es un nodo del árbol XML, escribe el resultado como cadena
        if (!result.isNode()) {
            System.out.println(result.getString());
            return;
        }
        //obtiene el nodo XML del resultado, el Item
        Node node = result.getNode();

        //prepara la serialización de otro arbol XML
        serializer.reset();
        //El método XMLSerializer.serializeToString se utiliza para obtener la 
        //representación de cadena de un nodo.
        String xmlForm = serializer.serializeToString(node);
        //Imprime el nodo del resultado como cadena
        System.out.println(xmlForm);
    }

    //método que cierra la conexión a la base de datos
    private static void cerrar(Library bd, LibraryManager bdManager)
            throws QizxException {
        //si la basede datos esta inmersa en una transacción, deshacer los cambios
        if (bd.isModified()) {
            bd.rollback();
        }
        //cerrar conexión con base de datos
        bd.close();
        //cerrar las bases de datos del grupo dentro de 100000 ms
        bdManager.closeAllLibraries(10000);
    }

    //método que devuelve la consulta XQuery (almacenada en fichero file)
    //en una cadena
    private static String cargaScript(File file)
            throws IOException {
        //Obtiene el contenido del fichero
        InputStreamReader in
                = new InputStreamReader(new FileInputStream(file), "UTF-8");
        //cadena para construir la cadena resultante
        StringBuilder build = new StringBuilder();
        //array de tamaño suficiente para alamcenar los caracteres del script
        char[] chars = new char[8192];
        int count;
        try {
            //mientras hay caracteres en el contenido del fichero
            //los añade a la cadena
            while ((count = in.read(chars, 0, chars.length)) != -1) {
                if (count > 0) {
                    build.append(chars, 0, count);
                }
            }
        } finally {
            in.close();
        }
        return build.toString();
    }
}
