package Ejercicio1;

/**
 * ****************************************************************************
 *
 *
 * @author IMCG
 */
//Interfaces y clases para gestionar la BD XML, las colecciones y documentos
import com.qizx.api.Collection;
import com.qizx.api.Configuration;
import com.qizx.api.Library;
import com.qizx.api.LibraryManager;
import com.qizx.api.LibraryMember;
import com.qizx.api.QizxException;
//Interfaces y clases para el procesamiento y análisis de documentos XML

import org.xml.sax.SAXException;

//Interfaces y clases para gestionar archvios y flujos de datos
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class xmlGenera {

    //Valores que se establecen para la creación de la base de datos XML que
    //se creará y los colecciones que contendrá
    //ruta del Grupo de bases de datos (Library Group)
    private static final String directorioGrupoRoot = "dbxml";
    //Nombre de Bilioteca o BD (Library): Tutorial
    private static final String bdNombre = "Cursillos";
    //ruta raíz colecciones de la BD o Library
    private static final String ruta = "/";
    //ruta de las colecciones origen a importar
    private static final String datosOrigenRoot = "BDCursillosXML/cursillos_datos/";
    //array con nombres de las colecciones origen a importar
    private static final String[] datosOrigenNombre = {"Aulas", "Cursos", "Profesores"};
    //filtro de extensiones de los ficheros (documentos) a importar: xml y xhtml
    private static final String extensionFiltro = "xml,xhtml";

    public static void main(String[] args) throws IOException, QizxException, SAXException {
        //variables locales
        String nombre;
        //filtro de ficheros
        FileFilter filtro = new FiltroFichero(extensionFiltro);
        //crea el objeto file directorioGrupo apuntando a esa ruta
        File directorioGrupo = new File(directorioGrupoRoot);
        //obtiene un grupo de bibliotecas
        LibraryManager bdManager = obtenerBDManager(directorioGrupo);
        //crea una bilbioteca o BD
        Library bd = obtenerBD(bdManager, bdNombre);
        //crea objeto miembro con la ruta absoluta
        LibraryMember miembroBD = bd.getMember(ruta);

        //comprueba si el miembro es una colección válida
        boolean miembroEsColeccion = (miembroBD != null && miembroBD.isCollection());

        //si no es una colección, cierra la BD bd y el grupo bdManager, y
        //muestra mensaje
        if (!miembroEsColeccion) {
            cerrar(bd, bdManager);
            System.out.println("'" + ruta + "', no existe o no es una colección");
        }

        try {
            //para cada miembro
            for (int i = 0; i < datosOrigenNombre.length; i++) {
                //accede a la colección fuente para guardar su ruta y nombre
                File fichero = new File(datosOrigenRoot + datosOrigenNombre[i]);

                //si es un miembro colección, guarda en 'nombre' su nombre antecedido por /
                if (miembroEsColeccion) {
                    nombre = ruta + datosOrigenNombre[i];
                    //el método llenar() vincula a la base de datos bd, la coleccion
                    //localizada en 'fichero', denominada 'nombre', conteniendo los
                    //documentos según filtro
                    llenar(bd, fichero, filtro, nombre);
                }
            }
            System.out.println("Confirmados cambios...");
            //Confirma las operaciones de la transacción
            bd.commit();
        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
        } finally {
            //cierra o realiza la desconexión de la BD bd
            cerrar(bd, bdManager);
        }
    }

    /**
     * ***************************************************************************
     * método que crea el directorio asociado al grupo de BDs si no existe
     * devuelve el direcotrio de alamacenamiento asociado al grupo de BDs
     *
     * @param directorioGrupo
     * @return
     * @throws IOException
     * @throws QizxException
     */
    private static LibraryManager obtenerBDManager(File directorioGrupo) throws IOException, QizxException {
        //objeto para gestionar el directorio asociado al grupo de bibliotecas
        //si existe el directorio asociado al grupo, devuelve el manejador
        //LibraryManager asociado
        if (directorioGrupo.exists()) {
            return Configuration.openLibraryGroup(directorioGrupo);
            //si no existe el directorio, intenta crearlo, y devuelve el manejador
            //LibraryManager asociado
        } else {
            if (!directorioGrupo.mkdirs()) {
                throw new IOException("no se puede crear directorio '" + directorioGrupo
                        + "'");
            }
            System.out.println("creando el Grupo de BDs en '" + directorioGrupo + "'...");
            return Configuration.createLibraryGroup(directorioGrupo);
        }
    }

    /**
     * **************************************************************************
     * método que abre la base de datos XML, y si no existe la crea y la abre
     * devuelve la conexión a la base de datos
     *
     * @param bdManager
     * @param bdNombre
     * @return
     * @throws QizxException
     */
    //Método que obtiene la conexión a una bd XML
    private static Library obtenerBD(LibraryManager bdManager, String bdNombre) throws QizxException {
        //Abre una conexión a la BD XML de nombre  bdNombre
        Library bd = bdManager.openLibrary(bdNombre);
        //Si no se ha abierto la BD (porque no existe)
        if (bd == null) {
            System.out.println("Creando BD XML '" + bdNombre + "'...");
            //Crea la BD XML
            bdManager.createLibrary(bdNombre, null);
            //Abre una conexión a la BD creada
            bd = bdManager.openLibrary(bdNombre);
        }
        //devuelve la conexión
        return bd;
    }

    /**
     * ****************************************************************************
     * método que crea las colecciones en la base de datos bd y llena cada
     * colección con documentos y/o colecciones según filtro
     *
     * @param bd: bd XML
     * @param fichero: ruta colección origen
     * @param filtro: filtro para documentos
     * @param ruta: ruta colección o documento destino
     * @throws IOException
     * @throws QizxException
     * @throws SAXException
     */
    private static void llenar(Library bd, File fichero, FileFilter filtro,
            String ruta)
            throws IOException, QizxException, SAXException {
        if (fichero.isDirectory()) { //si es directorio
            //obtiene la colección de la BD bd situada en esa ruta
            Collection coleccion = bd.getCollection(ruta);
            //si no existe la colección, crea la colección
            if (coleccion == null) {
                System.out.println("Creando colección '" + ruta + "'...");
                coleccion = bd.createCollection(ruta);
            }
            //Guarda en files, los ficheros con extensión coincidente en el filtro
            File[] files = fichero.listFiles(filtro);
            if (files == null) {
                throw new IOException("Error al listar directorio '" + fichero + "'");
            }
            //para cada fichero lo incluye en su correspondiente colección de la BD
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                llenar(bd, file, filtro, ruta + "/" + file.getName());
            }
            //si no es un directorio, lo importa como documento XML analizándolo
        } else {
            System.out.println("Importando '" + fichero + "' como documento '" + ruta
                    + "'...");
            //importa a bd, en la posición indicada por ruta, el documento XML
            // cuyo texto XML está en fichero
            bd.importDocument(ruta, fichero);
        }
    }

    //método que cierra la conexión a la base de datos
    private static void cerrar(Library bd, LibraryManager bdManager)
            throws QizxException {
        //Si la base de datos está inmersa en una transacción
        if (bd.isModified()) {
            //deshace los cambios realizados por la transacción
            bd.rollback();
        }
        //cierra la conexión a la base de datos bd
        bd.close();
        //cierra las bases de datos del grupo después de 10000 ms
        bdManager.closeAllLibraries(10000 /*ms*/);
    }
}
