package android.cloud.microsoft.com.serviceapplication;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by mohit on 20/7/17.
 */

/*
* FileList helps in obtaining the attributes required to be set in FilePOJO. Once set it can be obtained later.
*
* */
public class FileList {


    //state tells weather sd card is present or not.
    String state = Environment.getExternalStorageState();
    //data array will consist of n*3 number of elements for file name, image namne and detail depending on file or folder.
    private  String [][] data;
    //this is the root folder name, all files in this folder will be shown in the layout.
    String fileName;


    public FileList(String fileName)
    {
        this.fileName = fileName;
    }


    //data[i][0] has base file name
    //data[i][1] has size/no. of files
    //data[i][2] has extension name.or "folder" in case of folder.
    public String [][] getFileList()
    {

     if(Environment.MEDIA_MOUNTED.equals(state))

        {

        //This provides the path of Music Directory as given in my phone. emulated/0/music/


            final File musicFilePath;

            // if initial path is root then it means root of the shared folder. else whatever folder is clicked on under
            // root shared folder.
            if(fileName.compareTo("root")==0)
            musicFilePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC);
            else
            {
                musicFilePath = new File(fileName);
            }
            Log.i("CustomError",musicFilePath.toString());


            File [] fileList =musicFilePath.listFiles();


           data  = new String[fileList.length][3];

            for(int i =0; i< fileList.length;i++)
            {
                String baseName[]=  fileList[i].toString().split("/");
                data[i][0] = baseName[baseName.length-1];

                if(fileList[i].isDirectory())
                {
                    data[i][1] = Integer.toString(fileList[i].listFiles().length)+" files";
                    data[i][2] = "folder";
                }
                else
                {
                    data[i][1] = Integer.toString((int)fileList[i].length()/1024)+" Kb";
                    int length = fileList[i].toString().length();
                    data[i][2] = fileList[i].toString().substring(length-3,length);
                }
            }

        }
     else
        {
            Log.i("CustomError", "SD card not inserted");
        }
        return data;
    }
}


