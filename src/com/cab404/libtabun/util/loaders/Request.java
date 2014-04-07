package com.cab404.libtabun.util.loaders;

import com.cab404.libtabun.facility.ResponseFactory;
import com.cab404.libtabun.util.RU;
import com.cab404.libtabun.util.U;
import com.cab404.libtabun.util.modular.AccessProfile;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author cab404
 */
public abstract class Request {


    public abstract HttpRequestBase getRequest(AccessProfile accessProfile);

    public abstract void response(ResponseFactory.Parser parser, AccessProfile profile);
    public abstract ResponseFactory.Parser getParser();


    public void fetch(AccessProfile accessProfile, ResponseFactory.StatusListener statusListener) {
        HttpRequestBase request = getRequest(accessProfile);

        statusListener.onResponseStart();
        HttpResponse response;
        try {
            response = RU.exec(request, accessProfile);
            if (response.getStatusLine().getStatusCode() / 100 != 2) {
                ErrorResponse resp = new ErrorResponse(response.getStatusLine().toString());
                resp.setStatusLine(response.getStatusLine());
                statusListener.onResponseFail(resp);
            }
        } catch (Throwable e) {
            U.w("Page: Response fail");
            U.w(e);
            statusListener.onResponseFail(e);
            return;
        }
//        U.v(response.getStatusLine());
        statusListener.onResponseFinished();

        statusListener.onLoadingStarted();
        ResponseFactory.Parser parser = getParser();
        try {
            ResponseFactory.read(response, parser, statusListener);
        } catch (Throwable e) {
            U.w("Page: Loading fail");
            U.w(e);
            statusListener.onLoadingFail(e);
        }
        statusListener.onLoadingFinished();

        statusListener.onParseStarted();
        try {
            response(parser, accessProfile);
        } catch (Throwable e) {
            U.w("Page: Parsing fail");
            U.w(e);
            statusListener.onParseFail(e);
            return;
        }
        statusListener.onParseFinished();

        statusListener.onFinish();

    }

    /**
     * Downloads and parses page.
     */
    public void fetch(AccessProfile accessProfile) {
        fetch(accessProfile, new ResponseFactory.StatusListener() { });
    }

    public static class ErrorResponse extends Exception {
        StatusLine error;
        public ErrorResponse(String s) {
            super();
        }
        private void setStatusLine(StatusLine line) {
            this.error = line;
        }
        public StatusLine getStatusLine() {
            return error;
        }
    }
}
