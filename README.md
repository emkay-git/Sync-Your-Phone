# Sync-Your-Phone
This combination of android app and python script helps you keep a folder of your system synced with your phone given that both are in the
same network. More like a local one drive/google drive.
I personally use it mostly to sync mp3 files, which I donwload using [AutoDownloader](https://github.com/emkay-git/AutoDownloader),
with my phone. But you can transfer any type of file to your phone.</br>
Android App front end</br>
<p align="center">
<img height="auto" width="300" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/created.jpg"/>
<img height="auto" width="300" hspace="30" src="https://github.com/emkay-git/Sync-Your-Phone/blob/master/images/destroyed.jpg"/>
</p>
<p align="center">
<i>On the front end, one can start the service and close the app or stop the service to halt the app</i>
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

## Usage
1. Run the python script on the server side as `python songClient.py`. Keep it in the same folder which will act as shared folder.
2. Install the app in your phone through android studio. (Right now not avaible on playstore.)

## Suggested Improvement(s)
* To transfer file to the phone requires ip address of the phone in python script which is done by manually. So if IP address changes
for phone then I need to manually change it in the python script. Automation of this task is needed, where script itself search
for devices on the same network and connect to it.
* It's a single side transfer that is from your desktop/laptop to phone. Other way round can be built, using the same programs or
different program.
* App uses a button to start the service. App should start the service automatically on startup of phone.
* More options may be added in the app to make user customization better.
* Make app good enough to be packaged into android store.

## Contribute
Fork, make pull requests, open issues, give more ideas and suggestion to improve this app so that ultimately it can be put on playstore.

## License
This project is licensed under [MIT License](https://github.com/emkay-git/Sync-Your-Phone/blob/master/LICENSE)
