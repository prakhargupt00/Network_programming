#TCP has 3 way handshake 
import argparse, socket

def recvall(sock, length):
    data = b''
    while len(data) < length:
        more = sock.recv(length - len(data))
        if not more:
            raise EOFError('was expecting %d bytes but only received'
                           ' %d bytes before the socket closed'
                           % (length, len(data)))
        data += more
    return data

def server(interface, port): # Activities : 1 Binding  2 listening  3 accepting
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  #AF_INET : Internet address family  TCP is stream based protocol.
    #In TCP we et acknowlegment of bytes being recieved not packets of data   
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)#so as to tell the address could be reused by some other application after connection closed
    sock.bind((interface, port))            #Also if we close server and open it again we get no error in client since we are using reusable addr
    sock.listen(1) #socket is open to listen to any request  
    print('Listening at', sock.getsockname())  # getsocketname() Get info about your own socket 
    while True:
        sc, sockname = sock.accept()  #accept a request for connection 
        print('We have accepted a connection from', sockname)
        print('  Socket name:', sc.getsockname())
        print('  Socket peer:', sc.getpeername()) #Get info of peer's socket 
        message = recvall(sc, 16) #Recieve all 
        print('  Incoming sixteen-octet message:', repr(message))
        sc.sendall(b'Farewell, client')  #Send all 
        sc.close()
        print('  Reply sent, socket closed')


def client(host, port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    print('Client has been assigned socket name', sock.getsockname())
    sock.sendall(b'Hi there, server')
    reply = recvall(sock, 16)
    print('The server said', repr(reply))
    sock.close()

if __name__ == '__main__':
    choices = {'client': client, 'server': server}
    parser = argparse.ArgumentParser(description='Send and receive over TCP')
    parser.add_argument('role', choices=choices, help='which role to play')
    parser.add_argument('host', help='interface the server listens at;'
                        ' host the client sends to')
    parser.add_argument('-p', metavar='PORT', type=int, default=1060,
                        help='TCP port (default 1060)')
    args = parser.parse_args()
    function = choices[args.role]
    function(args.host, args.p)