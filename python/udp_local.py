import argparse, socket  
from datetime import datetime

MAX_BYTES = 65555

#socket act as interface at user level  and kernal level 

#server always listens 

def server(port):
    #creating the socket object 
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)  # socket AF_INET  :internet family of protocols telling to use internet address  SOCK_DGRAM : socket datagram --> UDP protocol 
    sock.bind(('127.0.0.1', port))   #binding the socket to server's own IP address and the port because a machine can have multiple IP address..
    print('Listening at {}'.format(sock.getsockname())) # getsockname returns socket details(IP Address and PORT no) which address it is bind to .. 
    while True:
        data, address = sock.recvfrom(MAX_BYTES)     #socket recieve data   (max data size is 65555) and address of the client 
        text = data.decode('ascii')
        print("The client at {} says {!r}".format(address,text))
        text = 'Your data was {} bytes long'.format(len(data))
        data = text.encode('ascii')
        sock.sendto(data, address)   #socket object is used to  send the data to the adderss of client 

#client makes request but  we need not bind it to any port 
def client(port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    # sock.bind(('127.0.0.1',40230))    #we can do this also but not needed because then  port will be automatically assigned if free  
    text = 'The time is {}'.format(datetime.now())
    data = text.encode('ascii')
    sock.sendto(data, ('127.0.0.1', port))  #server IP address 
    print('The OS assigned me the address {}'.format(sock.getsockname()))
    data, address = sock.recvfrom(MAX_BYTES)
    text = data.decode('ascii')
    print('The server {} replied {!r}'.format(address, text))

if __name__ == '__main__':
    choices = {'client': client, 'server': server}
    parser = argparse.ArgumentParser(description = 'Send and Receive UDP locally')
    parser.add_argument('role', choices=choices, help = 'which role to play')
    parser.add_argument('-p', metavar='PORT', type=int, default = 1060, help = 'UDP port (default 53)')
    args = parser.parse_args()
    function = choices[args.role]
    function(args.p)


