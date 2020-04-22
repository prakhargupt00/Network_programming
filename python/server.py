import socket
import datetime
 

localIP     = "127.0.0.1"

localPort   = 20001

bufferSize  = 1024

 

msgFromServer       = "Hello UDP Client"
bytesToSend         = str.encode(msgFromServer)

limit = 50

rate={}
time={}
# Create a datagram socket

UDPServerSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
 

# Bind to address and ip

UDPServerSocket.bind((localIP, localPort))
 

print("UDP server up and listening")

 

# Listen for incoming datagrams

while(True):

    bytesAddressPair = UDPServerSocket.recvfrom(bufferSize)

    message = bytesAddressPair[0]

    address = bytesAddressPair[1]

    msg_len = len(message)
    print(msg_len)





    clientMsg = "Message from Client:{}".format(message.decode('utf-8'))

    print(address[1])

    print(clientMsg)

    if address[1] in rate.keys():
    	if rate[address[1]] + msg_len < limit:
    		rate[address[1]] += msg_len
    	else:
    		now = datetime.datetime.now()
    		now_plus_1 = now + datetime.timedelta(minutes = 1)
    		if address[1] in time.keys():
    			if now > time[address[1]]:
    				rate[address[1]] = msg_len
    				time[address[1]] = now_plus_1
    			else:
    				UDPServerSocket.sendto(str.encode("Rate Limit Exceeded"), address)		
    		else:
    			time[address[1]] = now_plus_1
    			UDPServerSocket.sendto(str.encode("Rate Limit Exceeded"), address)	
    		
    		
    else:
    	rate[address[1]] = msg_len