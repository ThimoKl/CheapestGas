package de.einfachtanken;

import com.google.gson.Gson;

/**
 * Created by thimokluser on 4/22/15.
 */
public class GasStationDownloader implements DownloadListener {

    private ParserListener mListener = null;
    private String mUrl = "https://creativecommons.tankerkoenig.de/json/list.php?lat=[LAT]&lng=[LNG]&rad=[RADIUS]&sort=price&type=[TYPE]&apikey=5b38b277-eb17-4c16-b196-0d8751ef1807";
    private String mLat;
    private String mLng;
    private String mType;
    private boolean mRepeated = false;

    public GasStationDownloader(String lat, String lng, String type, ParserListener listener) {
        mListener = listener;
        mLat = lat;
        mLng = lng;
        mType = type;
        mRepeated = false;

        (new HttpGetTask(this, getUrl())).execute();
    }

    private String getUrl() {
        String url = mUrl;
        url = url.replace("[LAT]", mLat);
        url = url.replace("[LNG]", mLng);
        url = url.replace("[TYPE]", mType);
        if(mRepeated) url = url.replace("[RADIUS]", "8");
        else url = url.replace("[RADIUS]", "4");
        return url;
    }

    @Override
    public void onFileDownloaded(String jsonString) {
        if(mListener == null) return;
        Gson gson = new Gson();
        try {
            GasStationJsonWrapper wrapper = gson.fromJson(jsonString, GasStationJsonWrapper.class);
            if((wrapper.stations == null || wrapper.stations.size() == 0 ) && !mRepeated) {
                mRepeated = true;
                (new HttpGetTask(this, getUrl())).execute();
            }
            mListener.onGasStationsParsed(wrapper.stations);
        } catch(Exception e) {
            onFailed(e);
        }
    }

    @Override
    public void onFailed(Throwable e) {
        if(mListener == null) return;
        mListener.onGasStationParserFailed(e);
    }

}
