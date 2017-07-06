#!/usr/bin/env /usr/bin/python
import socket                                         
import time
import os
from os import listdir
from os.path import isfile, join
import json
import subprocess,struct


def getMachineIPAddress():
    #connecting with google nameserver and then finding ip of this machine.
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    ip =  s.getsockname()[0]
    s.close()
    return ip
    

def getNetworkAddress():
    #string manipulation to get subnet
    machineIp = getMachineIPAddress()
    index = machineIp.rfind('.')
    networkAddress = machineIp[:index+1]
    return networkAddress


def getActiveIPAddress():
    #using nmap and run it in the shell, which gives the active devices in the network. Then some
    # string manipulation to get the ip address of those active devices.
    networkAddress = getNetworkAddress()
    proc=subprocess.Popen('nmap -sP '+networkAddress+'0/24', shell=True, stdout=subprocess.PIPE, )
    output=proc.communicate()[0]
    print output
    activeIp = []
    outputArray = output.split('\n')
    for out in outputArray:
        if out.startswith("Nmap scan report for"):
            ipOutputArray = out.split(' ')
            ipOutput = ipOutputArray[len(ipOutputArray)-1]
            if ipOutput.endswith(')') and ipOutput.startswith('('):
                ipOutput = ipOutput[1:len(ipOutput)-1]
            activeIp.append(ipOutput)
    return activeIp


def sendFileToPhone(mp3files,serversocket):
    

    # to send file to the phone which is acting as server, connection has to be made. One may not know
    # what the IP of the phone is, when coming in the same network, so try to connect to to all the active IPs.
    connectFlag = False
    activeIps = getActiveIPAddress()
    for node in activeIps:

     #looping through all the active ip address on the subnet and trying a connection.
  
        print "Trying to connect "+node
        try:

            port = 9998  

            serversocket.connect((node, port)) 
            print "Connection Established......"
            connectFlag = True
            break;
        except socket.error as serr:
            pass
            print serr
            print "Failed Establishing Connection....2"
   
    # #In case all nodes were scanned but no connection was established, we start again.
    # #But if connection is made we continue to send files
    if not connectFlag:
        sendFileToPhone(mp3files,serversocket)
        #since it becomes a recursive call need to return if any of the operation is successfull.
        return



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
            a = serversocket.send(l)
            
            l=f.read(1024*128)
           
            print "Transferring data"
        print "File Transferred"


        ack=serversocket.recv(2048)
        print ack
        
        os.rename(mp3,destinationFolder+os.path.basename(mp3))
        
        

    serversocket.shutdown(socket.SHUT_RDWR)
    serversocket.close()



while True:
    mp3files = [f for f in listdir(".") if f.endswith((".pdf",".zip",".mp3",".webm"))]
    if len(mp3files) == 0:
        print "No files"
        time.sleep(20)
    else:
        print "Files Found"

        #this set of try catch will handle error at time of file transfer.
        try:
            print "Establishing Connection...."
            serversocket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM)
            
            # so_sndtime to ask for non blocking operation in case send statement blocks for some reason and restart the process all
            # if phone out of network, send statements blocks. using so_sndtime restarts the process.
            # over again.  Using so_sndtime gives three error if message send/receive or connect fails after setout time.
            # EAGAIN - "Resource temporarily unavailable" https://stackoverflow.com/questions/14370489/what-can-cause-a-resource-temporarily-unavailable-on-sock-send-command
            # EINPROGRESS - "Operation Now in progress" at the call() method https://stackoverflow.com/questions/6202454/operation-now-in-progress-error-on-connect-function-error
            # EWOULDBLOCK - may be same as EAGAIN


            timeval = struct.pack('ll', 10, 10000)
            # serversocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            serversocket.setsockopt(socket.SOL_SOCKET,socket.SO_SNDTIMEO,timeval)
            sendFileToPhone(mp3files,serversocket)  
        except socket.error as serr:
            print "Error Message: "+ str(serr)
            print "Failed Establishing Connection....1"
            time.sleep(2)

