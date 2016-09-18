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
    host = socket.gethostname()
    port = 9999
    serversocket.bind((host, port))
    serversocket.listen(20)
    print ("serversocket is running,waiting client connect...")
    while True:
        clientsocket,addr = serversocket.accept()
        thread.start_new_thread(MutiServer,(clientsocket,addr))
        
if __name__ == '__main__':
    main()
