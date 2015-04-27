package de.einfachtanken;

/**
 * Download Listener
 * Provide this interface to be notified when the download is finished or an error occured
 */
public interface DownloadListener
{
    /**
     * One or multiple files have been downloaded.
     *
     * @param results Content of the file as String. One array element per file.
     */
    public void onFileDownloaded(String results);

    /**
     * The download has failed
     *
     * @param e The raised exception
     */
    public void onFailed(Throwable e);

}