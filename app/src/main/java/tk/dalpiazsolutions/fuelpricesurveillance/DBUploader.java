package tk.dalpiazsolutions.fuelpricesurveillance;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.Calendar;
import java.util.Locale;

public class DBUploader {

    private String address;
    private String user;
    private String password;
    private Context context;
    private File file;
    private InputStream is;
    private BufferedInputStream buffIn;
    private long time;
    private FTPClient ftpClient;
    private boolean state = true;

    public DBUploader(Context context)
    {
        this.context = context;
        address = context.getString(R.string.ip);
        user = context.getString(R.string.user);
        password = context.getString(R.string.password);
    }

    public boolean uploadDB()
    {
        ftpClient = new FTPClient();

        try {
            ftpClient.connect(address);
            ftpClient.login(user, password);
            ftpClient.changeWorkingDirectory(context.getString(R.string.directory));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            Calendar calendar = Calendar.getInstance();
            time = calendar.getTimeInMillis();

            //prices
            uploadFile(context.getString(R.string.DB_PATH), String.format(Locale.getDefault(), context.getString(R.string.fileName), time));
            uploadFile(context.getString(R.string.DB_PATH_SHM), String.format(Locale.getDefault(), context.getString(R.string.fileNameSHM), time));
            uploadFile(context.getString(R.string.DB_PATH_WAL), String.format(Locale.getDefault(), context.getString(R.string.fileNameWAL), time));

            //articles
            uploadFile(context.getString(R.string.DB_PATH_ARTICLE), String.format(Locale.getDefault(), context.getString(R.string.articleFileName), time));
            uploadFile(context.getString(R.string.DB_PATH_ARTICLE_SHM), String.format(Locale.getDefault(), context.getString(R.string.articleFileNameSHM), time));
            uploadFile(context.getString(R.string.DB_PATH_ARTICLE_WAL), String.format(Locale.getDefault(), context.getString(R.string.articleFileNameWAL), time));

            /*ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.fileName), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.fileName), time));
            buffIn.close();

            file = new File(context.getString(R.string.DB_PATH_SHM));
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.fileNameSHM), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.fileNameSHM), time));
            buffIn.close();

            file = new File(context.getString(R.string.DB_PATH_WAL));
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.fileNameWAL), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.fileNameWAL), time));
            buffIn.close();

            file = new File(context.getString(R.string.DB_PATH_ARTICLE));
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.articleFileName), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.articleFileName), time));
            buffIn.close();

            file = new File(context.getString(R.string.DB_PATH_ARTICLE_SHM));
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.articleFileNameSHM), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.articleFileNameSHM), time));
            buffIn.close();

            file = new File(context.getString(R.string.DB_PATH_ARTICLE_WAL));
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(String.format(Locale.getDefault(), context.getString(R.string.articleFileNameWAL), time), buffIn);
            Log.i("file", String.format(Locale.getDefault(), context.getString(R.string.articleFileNameWAL), time));
            buffIn.close();*/

            ftpClient.logout();
            ftpClient.disconnect();

            return state;
        } catch (IOException e) {
            Log.i("Exception", "EXCEPTION");
            e.printStackTrace();
            return false;
        }
    }

    public void uploadFile(String filepath, String destination)
    {
        try {
            file = new File(filepath);
            is = new FileInputStream(file);
            buffIn = new BufferedInputStream(is);
            ftpClient.storeFile(destination, buffIn);
            Log.i("file", destination);
            buffIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            state = false;
        } catch (IOException e) {
            e.printStackTrace();
            state = false;
        }
    }
}
