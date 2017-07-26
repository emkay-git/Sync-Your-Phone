package android.cloud.microsoft.com.serviceapplication;

/**
 * Created by mohit on 20/7/17.
 */

/* POJO is our lovely plain old java object. FilePOJO is used to get and set basics details of the files/folders to be
 displayed. It includes :-
 fileName - name of file/folder
 detail - it can be set with two options. If file then size in Kb. If folder then number of files.
 fileImage - name of the image file depending on the extension of files.
*/
public class FilePOJO {

    private String fileName;
    private String detail;
    private String fileImage;

    public String getDetail()
    { return detail; }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }


    public String getFileName()
    {
        return fileName;
    }


    public void setFileName(String fileName)
    { this.fileName =  fileName; }

    public void setFileImage(String fileImage)
    {
        this.fileImage = fileImage;
    }

    public String getFileImage()
    {
        return fileImage;
    }

}
