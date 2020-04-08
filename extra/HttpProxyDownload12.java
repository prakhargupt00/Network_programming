/* BeginGroupMemebers */
/* f20170142@hyderabad.bits-pilani.ac.in Avinash Narasimmhan */
/* f20170121@hyderabad.bits-pilani.ac.in Prakhar Gupta */
/* f20170142@hyderabad.bits-pilani.ac.in Avinash Narasimmhan */
/* f20170142@hyderabad.bits-pilani.ac.in Avinash Narasimmhan */
/* EndGroupMembers */
/* Brief description of program...*/
/* ... */

import java.io.*;
import java.net.*;
class HttpProxyDownload
{
	public static void main(String[] args)
	{		
			String link = args[0];
			String IP = args[1];
			int port = Integer.parseInt(args[2]);
			String login = args[3];	
			String password = args[4];
			String htmlpage = args[5];
			String logoname = args[6];
			try 
			{
            	Socket socket = new Socket(link, 80);
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
           	 	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	out.println("GET / HTTP/1.1\nHost: " +link +"\n\n");
            	String inputLine;
            	while ((inputLine = in.readLine()) != null) 
            	{
                	System.out.println(inputLine);
            	}
        	} 
        	catch (UnknownHostException e) 
        	{
            	System.err.println("Don't know about host " + link);
            	System.exit(1);
        	} 
        	catch (IOException e) 
        	{
            	System.err.println("Couldn't get I/O for the connection to " + link);
            	System.exit(1);
        	} 
    }
}