package de.einfachtanken;

import java.util.List;

/**
 * Download Listener
 * Provide this interface to be notified when the download is finished or an error occured
 */
public interface ParserListener
{
    /**
     * One or multiple files have been downloaded.
     *
     * @param results Content of the file as String. One array element per file.
     */
    public void onGasStationsParsed(List<GasStation> stations);

    /**
     * The download has failed
     *
     * @param e The raised exception
     */
    public void onGasStationParserFailed(Throwable e);

}