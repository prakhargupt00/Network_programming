import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
 
public class Image{
 
   public static void main(String[] args) throws IOException {
       String link = "https://www.google.com";
       File htmlFile = new File("Output.html");
       Document doc = Jsoup.parse(htmlFile, "UTF-8",link);
       String title = doc.title();
       System.out.println("Title : " + title);
       Element content = doc.getElementById("hplogo");
       Elements image = doc.select("img");
            
            for(Element imageElement : image){
            
            //make sure to get the absolute URL using abs: prefix
            String strImageURL = imageElement.attr("src");
            System.out.println(strImageURL);
            
            //download image one by one
            downloadImage(link+"/"+strImageURL);
            
        }
   }
   private static void downloadImage(String strImageURL){
        
       String strImageName="google";
        try {
            
            //open the stream from URL
            System.out.println(strImageURL);
            URL urlImage = new URL(strImageURL);
            InputStream in = urlImage.openStream();
            
            byte[] buffer = new byte[4096];
            int n = -1;
            String save = "E:/3-2/Computer Networks" + "/" + strImageName;
            
            OutputStream os = 
                new BufferedOutputStream(new FileOutputStream(save));
            
            //write bytes to the output stream
            while ( (n = in.read(buffer)) != -1 ){
                os.write(buffer, 0, n);
            }
            
            //close the stream
            os.close();
            
            System.out.println("Image saved");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
