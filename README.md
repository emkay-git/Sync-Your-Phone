# Sync-Your-Phone
This combination of android app and python script helps you keep a folder of your system synced with your phone given that both are in the
same network.
I personally use it mostly to sync mp3 files, which I donwload using [AutoDownloader](https://github.com/emkay-git/AutoDownloader),
with my phone. But you can transfer any type of file to your phone.</br>
Android App front end</br>
<p align="center">
<!-- <img height="auto" width="300" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/created.jpg"/>
<img height="auto" width="300" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/destroyed.jpg"/>
 -->
<img height="auto" width="350" src = "https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/service.gif"/>
</p>
<p align="center">
<i>Various options which are displayed depending on the state of the service, socket connection or wifi connection.</i>
</p>
Python script backend</br>
<p align="center">
</br>
<img height="auto" width="300" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/script1.png"/>
<img height="auto" width="370" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/script2.png"/>
</p>
<p align="center">
<i>At backend as soon as new file is found in the folder it makes the <b>socket</b> connection with the phone and transfers
the files.</i>
</p>

## Workflow
<p align="center">
<img height="auto" width="300"  src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/android.png"/>
</p>

## Requirements
On system side, whose folder is to be shared.
1. Python
2. Nmap `sudo apt-get install nmap` should do the work.

## Usage
1. Run the python script on the server side as `python songClient.py`. Keep it in the same folder which will act as shared folder. To run the script in background and on your system startup, you can use [upstart](https://stackoverflow.com/questions/24518522/run-python-script-at-startup-in-ubuntu).
2. Install the app in your phone through android studio. (Right now not avaible on playstore.)


##Caveats
This app is wifi network dependent. If you are on a good network, file transfer is really fast. But poor wifi network results blockage of data sometimes. Fastest way to transfer data is to connect the phone with system's wifi hotspot.
In any case if data transmission is slow. Do open an issue reporting it.

## Suggested Improvement(s)
* ~~To transfer file to the phone requires ip address of the phone in python script which is done by manually. So if IP address changes for phone then I need to manually change it in the python script. Automation of this task is needed, where script itself search for devices on the same network and connect to it.~~ Done! :heavy_check_mark: Using Nmap to scan the addresses. So one dependecy now is that system should have nmap for this app to work. Some other solution may be suggested.
* ~~Any file inside the shared folder will be transferred. Sending an entire folder to the phone hasn't been looked after
yet. That feature is to be taken care of.~~ Done! :heavy_check_mark:
* ~~Start/Stop the service when wifi is on/off because app should start service only when it's connected to wifi.~~ Done! :heavy_check_mark: 
* ~Some indication if phone is connected to laptop, transferring data etc.~ Done! :heavy_check_mark:
* Abilitiy to display files/folder in the app itself.
* Improve socket implementation
* It's a single side transfer that is from your desktop/laptop to phone. Other way round can be built, using the same programs/app or different program/app.
* Make app good enough to be packaged into android store.

## Contribute
Fork, make pull requests, open issues, give more ideas and suggestion to improve this app so that ultimately it can be put on playstore.

## License
This project is licensed under [MIT License](https://github.com/emkay-git/Sync-Your-Phone/blob/master/LICENSE)
