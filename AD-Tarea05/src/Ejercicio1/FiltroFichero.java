/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Ejercicio1;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author IMCG
 */
//Clase que implementa un filtro para extensiones de ficheros
  class FiltroFichero
          implements FileFilter {
//array para guardar las extensiones precedidas por '.'
    private String[] sufijo;
    private boolean noCoincide;
    public FiltroFichero(String patron) {
      noCoincide = false;
      //Guarda las extensiones de los ficheros a procesar
      String[] extension = patron.split(",");
      sufijo = new String[extension.length];
//Guarda cada extensión, precedida por punto
      for (int i = 0; i < extension.length; ++i) {
        String ext = extension[i];
          sufijo[i] = "." + ext;
      }
    }
    @Override
    public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      String nombre = file.getName();
      boolean res = false;
      for (int i = 0; i < sufijo.length; ++i) {
        if (nombre.endsWith(sufijo[i])) {
          res = true;
          break;
        }
      }
      return noCoincide ? !res : res;
    }
  }



