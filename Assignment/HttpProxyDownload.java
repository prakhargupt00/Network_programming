/* BeginGroupMembers */
/* f20170121@hyderabad.bits-pilani.ac.in Prakhar Gupta */
/* f20170142@hyderabad.bits-pilani.ac.in Avinash Narasimmhan */
/* f20170098@hyderabad.bits-pilani.ac.in Sri charan */
/* f20170272@hyderabad.bits-pilani.ac.in G Srichand */
/* EndGroupMembers */


/* Brief description of program...*/
/* Here we are  Java sockets to download the main page and logo from given website through a squid proxy, 
which has been set up by the instructors.We are first trying to create a socket connection to the given 
host and port .We do this by sending a proxy authorisation containing login and password in base 64 encoded 
format. After this once we have got 200 connection established we are cretaing a layered socket using 
SSL socket factory by creatin a trust all certificate store and then proving to the socket and finally it is 
going to start handshake once it got 200 ok then we are using output stream to send GET request and finally 
downloading the webpage from response by writing into the file.Next we are doing similar to send image request.
*/


import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.* ; 


public class HttpProxyDownload
{
    public static void main( String[] args ) throws Exception {

        /***********************
         Taking parameters from command line arguments 
         *********************/

       		String link = args[0];
			String host = args[1];
			int port = Integer.parseInt(args[2]);
			String login = args[3];	
			String password = args[4];
			String htmlpage = args[5];
			String logoname = args[6];
    
        try{ 

        // Socket object connecting to proxy
        Socket sock = new Socket(host, port);

        /***********************************
         * HTTP CONNECT protocol RFC 2616
         ***********************************/

        System.out.println("reached checkpoint 1") ; 

        String proxyConnect = "CONNECT " + link + ":443 HTTP/1.1\r\n" + "Host: "+link +":443\r\n" +"Proxy-Connection: keep-alive\r\n"  ;
        

        /***************************************
         Add Proxy Authorization if login and password is set
        **************************************/ 

        String proxyUserPass = String.format("%s:%s", login, password);

        proxyConnect  = proxyConnect.concat("Proxy-Authorization: Basic " + Base64.getEncoder().encodeToString(proxyUserPass.getBytes()) );
        
        proxyConnect = proxyConnect.concat("\r\n\r\n");
        
        System.out.println("reached checkpoint2 "+ proxyConnect) ; 

        sock.getOutputStream().write(proxyConnect.getBytes());
        
        /*******************************
        Creating output and input streams  
        *****************************************/

        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        PrintWriter file = new PrintWriter( new FileWriter(htmlpage) ); 
        String temp ; 

        System.out.println(in.readLine());

        /*******************************
        // Making ssl context and initialising it with trust store to accept all certificates required to form ssl socket 
        *****************************************/

        SSLContext sslContext = SSLContext.getInstance("SSL");
            
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String authType) throws CertificateException {
            }
        } }, new SecureRandom());

        System.out.println("Checkpoint 3 reached  ") ; 
        // SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        /**********************************
        Creating a layered SSL socket on top of normal socket  
        ***********************************/

        SSLSocketFactory factory = sslContext.getSocketFactory() ;

        SSLSocket sslsock = (SSLSocket) factory.createSocket(sock, null, sock.getPort(), false);

        sslsock.startHandshake();


        System.out.println("Checkpoint 4 reached working!! " ) ; 


        /**************************************************
        // Now use the secure socket just like a regular socket to send GET request
        *************************************************** */

        PrintWriter out = new PrintWriter(sslsock.getOutputStream()) ;
        String request = "GET / HTTP/1.1\r\n"+ "Host: "+ link+"\r\n\r\n" ; 

        out.print(request);
        out.flush();

        /*****************************
        Read response 
        ***************************/

        BufferedReader sslin = new BufferedReader(new InputStreamReader(sslsock.getInputStream()));
        Boolean headerEnded = false ; 
        String newmsg = "" ;

        while((temp = sslin.readLine())!= null){
        //    System.out.println(temp+"\n") ; 
           if(!headerEnded && (temp.indexOf("<!doctype html")>=0 || temp.indexOf("<!DOCTYPE html")>=0)){
               System.out.println("header ended") ;
               headerEnded = true ; 
           } 

           if(headerEnded)
           newmsg+=temp+"\n" ; 

           if(temp.indexOf("</html>")>=0)
           break ; 

        }

        // System.out.print(newmsg) ; 
        System.out.println("Webpage Completed !!") ; 
        file.println(newmsg) ; 

        System.out.println("checkpoint 5 finished!!") ;


        /************************
         Image Download
        ************************/

        String img = "/images/branding/googlelogo/1x/googlelogo_white_background_color_272x92dp.png" ; 

        String req = "GET "+  img + " HTTP/1.1\r\n"+ "Host: "+link+"\r\n"+"Content-Type: image/png" +"\r\n\r\n" ; 
        out.print(req) ; 
        out.flush() ; 
        
        byte[] pixels =  new byte[1024] ; 
        int t;
        Boolean headerStarted = false , headerEnd = false  ;  

        if(link.indexOf("google")>=0){
            FileOutputStream f = new FileOutputStream(logoname) ; 
            InputStream i =  sslsock.getInputStream() ; 

            while(( t = i.read(pixels, 0 ,pixels.length))!= -1){

                if(!headerStarted && (char)pixels[0] == 'H' && (char)pixels[1] == 'T' &&  (char)pixels[2] == 'T' && (char)pixels[3] == 'P'){
                    headerStarted = true ; 
                }
    
                if(!headerEnd && headerStarted){
                    // System.out.println("##################################") ; 
                    for(int ch=0 ;ch<1024;ch++){
                        if((char)pixels[ch] == '\r' && (char)pixels[ch+1] == '\n' &&  (char)pixels[ch+2] == '\r' && (char)pixels[ch+3] == '\n'){
                            headerEnd = true ;
    
                            for(int k=ch+4 ;k<1024;k++){
                                // System.out.print((char)pixels[k]) ; 
                                f.write(pixels[k]) ;
                            }
    
                            break ; 
                        }       
                    }
                }
                if(headerEnd)
                break ; 
            }
    
            /**********
            picture ends with IEND 
            ******************/
    
            while(( t = i.read())!= -1){
                // System.out.print((char)t ) ; 
                f.write((char)t) ;
    
                if((char)t == 'I'){
                    if(( t = i.read())!= -1){
                        f.write((char)t) ;
    
                        if((char)t == 'E'){
                            if(( t = i.read())!= -1){
                                f.write((char)t) ;
    
                                if((char)t == 'N'){
                                    if(( t = i.read())!= -1){
                                        f.write((char)t) ;
    
                                        if((char)t == 'D'){
                                            break ; 
                                            
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    
            }
            i.close();
            f.close();
        }

        System.out.println("image download finished") ; 

        
        /********************
         CLOSING ALL SOCKET ANDS FILES 
        ******************** */

        in.close() ;
        sslin.close() ;  
        file.close() ; 
        sock.close() ; 
        sslsock.close() ;

        } catch ( Exception e){
            System.out.println("Exception caught "+ e) ; 
        }
    }
}

 
