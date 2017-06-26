#!/usr/bin/env /usr/bin/python
import socket                                         
import time
import os
from os import listdir
from os.path import isfile, join
import json


def sendFileToPhone(mp3files,serversocket):
    
    #ip address of your phone where you wish to send the files.
    host = "192.168.23.21"  
    #                     
    port = 9998  

    serversocket.connect((host, port)) 
    print "Connection Established......"

   
    # I made this program to transfer mp3 files from my system to phone. So it's writtent in context to that.. but it can be done
    # for any general file 
    for mp3 in mp3files:
        
        #Preparing MetaData         
        metaData={"mp3Name":os.path.basename(mp3),"mp3Size":str(os.path.getsize(mp3))}
        metaData=json.dumps(metaData)
        print (metaData)
        print "Sending MetaData......"
        serversocket.send(metaData+"\n")
        print "MetaData sent........."
        

        ack=serversocket.recv(2048)
        print ack
        
        
        print "Sending mp3 File......"        
        f= open(mp3,'r+b')
        l=f.read(1024*128)
        while(l):
            serversocket.send(l)
            l=f.read(1024*128)
            print "Transferring data"
        print "File Transferred"


        ack=serversocket.recv(2048)
        print ack
        
        # you can use os.rename to move the file just transferred to the phone from current folder to any other destination folder.
        #os.rename(mp3,destination directory +os.path.basename(mp3))
        # destination directory is the folder where you wish to send the files to
        
        

    serversocket.shutdown(socket.SHUT_RDWR)
    serversocket.close()


# Continously checks if there is any file in the folder which is syced with the phone. If there is then it makes a socket connection and
# send it to the phone immediately.

while True:
    #Apart from sending mp3 files you can add different extension formats to send the files.
    #listdir('.') assumes that all files are in the same folder as this script, you can change it to whichever folder you would to sync.
    mp3files = [f for f in listdir('.') if f.endswith((".pdf",".zip",".mp3",".webm"))]
    if len(mp3files) == 0:
        print "No files"
        time.sleep(20)
    else:
        print "Files Found"
        try:           
            print "Establishing Connection...."
            serversocket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM)

            serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            sendFileToPhone(mp3files,serversocket)
        except socket.error as serr:
            print serr
            print "Failed Establishing Connection...."
            time.sleep(2)

