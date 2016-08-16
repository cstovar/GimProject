
package co.com.gimproject.controladores.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class UtilJsf {
  public static String guardaBlobEnFicheroTemporal (byte[] bytes, String nombreArchivo){
  String ubicacionImagen = null;
      ServletContext servletcontext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
      String path = servletcontext.getRealPath("") + File.separatorChar + "resources" + File.separatorChar + "img" + File.separatorChar + nombreArchivo;
      
      File f;
      InputStream in;
      
      try {
          f = new File(path);
          in = new ByteArrayInputStream(bytes);
      try (FileOutputStream out = new FileOutputStream(f.getAbsolutePath())) {
          int c = 0;
          
          while ((c = in.read()) >= 0) {              
              out.write(c);
          }
          out.flush();
      }
          ubicacionImagen = "../../resources/img/tmp" + nombreArchivo;
      } catch (Exception e) {
          System.err.println("no se pudo cargar la imagen");
          e.getMessage();
      }
      return ubicacionImagen;
  }  
}
