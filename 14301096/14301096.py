import socket,thread
from time import sleep

def  Server(clientsocket,addr):
    print ("CLIENTIP: %s" % str(addr))
    msg = clientsocket.recv(1024)
    print (msg)
    client_message = msg
    clientsocket.send(msg[::-1])
    sleep(1024)
    clientsocket.close()
    print ("from %s connect is closed"% addr)
    
def main():
    serversocket = socket.socket() 
    host = '172.0.0.1'
    port = 3333
    serversocket.bind((host, port))
    serversocket.listen(20)
    print ("serversocket is running,waiting client connect...")
    while True:
        clientsocket,addr = serversocket.accept()
        thread.start_new_thread(MutiServer,(clientsocket,addr))
        
if __name__ == '__main__':
    main()
