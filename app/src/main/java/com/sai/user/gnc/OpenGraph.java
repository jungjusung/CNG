package com.sai.user.gnc;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.regex.Pattern;

public class OpenGraph {

    private static final String DEFAULT_TAG = OpenGraph.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;
    static String TAG;

    public interface Logger {
        void log(String tag, String msg);
    }

    public static class Builder {

        private String url;
        private String tag = DEFAULT_TAG;
        private Logger logger;
        private int timeoutMillis = DEFAULT_TIMEOUT_MILLIS;

        public Builder(String url) {
            this.url = url;
            TAG = this.getClass().getName();
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder logger(String tag, Logger logger) {
            this.tag = tag;
            this.logger = logger;
            return this;
        }

        public Builder timeoutMillis(int timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public OpenGraph build() {
            OpenGraph og = new OpenGraph();
            og.url = url;
            og.tag = tag == null ? DEFAULT_TAG : tag;
            og.logger = logger;
            og.timeoutMillis = timeoutMillis;
            return og;
        }
    }

    /*

     */

    private String tag;
    private Logger logger;
    private String url;
    private int timeoutMillis;

    private OpenGraph() {
    }


    /*
        data
     */

    public OpenGraphData getOpenGraph(String src) throws IOException {


        Document document = Jsoup.parse(src);
        document.setBaseUri(url);

        final OpenGraphData data = new OpenGraphData();
        data.html = src;
        data.url = url;
        data.domain = getDomain(url);

        data.title = getTitle(document);
        data.description = getDescription(document);
        data.image = _getImage(document);

        return data;
    }

    private String _getImage(Document doc) {
        // <meta property="og:image" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:image");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the image : <meta property=\"og:image\" content=\"*\" />");
                return getValidPath(text);
            }
        }
        for (Element e : doc.getElementsByTag("meta")) {
            if (e.hasAttr("content")) {
                final String text;
                try {
                    text = getValidPath(e.attr("content"));
                    //                Log.d(TAG,"원본"+text);
//                Log.d(TAG,"meta "+text.substring(text.lastIndexOf(".")));
                    if (text.substring(text.lastIndexOf(".")).equals(".png") || text.substring(text.lastIndexOf(".")).equals(".jpg")) {
                        if (isNotEmpty(text)) {
                            return getValidPath(text);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        for (Element e : doc.getElementsByTag("link")) {
            if (e.hasAttr("href")) {
                final String text;
                try {
                    text = getValidPath(e.attr("href"));
                    Log.d(TAG, "link " + text.substring(text.lastIndexOf(".")));
                    if (text.substring(text.lastIndexOf(".")).equals(".png") || text.substring(text.lastIndexOf(".")).equals(".jpg")) {
                        if (isNotEmpty(text)) return getValidPath(text);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        for (Element e : doc.getElementsByTag("link")) {
            if (e.hasAttr("rel")) {
                final String text;
                try {
                    text = getValidPath(e.attr("rel"));
                    Log.d(TAG, "link " + text.substring(text.lastIndexOf(".")));
                    if (text.substring(text.lastIndexOf(".")).equals(".png") || text.substring(text.lastIndexOf(".")).equals(".jpg")) {
                        if (isNotEmpty(text)) return getValidPath(text);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }
        }
        // 2nd -> img in div
        for (Element e1 : doc.getElementsByTag("div")) {
            if (e1.children().size() > 0) {
                e1 = e1.child(0);
                if (e1.tagName().equals("img")) {
                    if (e1.hasAttr("width")) {
                        final String text = getValidPath(e1.attr("src"));
                        if (isNotEmpty(text)) return getValidPath(text);
                    }
                }
            }
        }

        // 2nd -> img in p
        for (Element e1 : doc.getElementsByTag("p")) {
            for (Element e2 : e1.getElementsByTag("img")) {
                if (e2.hasAttr("src")) {
                    final String text = getValidPath(e2.attr("src"));
                    if (isNotEmpty(text)) return getValidPath(text);
                }
            }
        }


        // 2nd -> img in dd
        for (Element e1 : doc.getElementsByTag("dd")) {
            for (Element e2 : e1.getElementsByTag("img")) {
                if (e2.hasAttr("src")) {
                    final String text = getValidPath(e2.attr("src"));
                    if (isNotEmpty(text)) return getValidPath(text);
                }
            }
        }

        // 3rd -> img in html
        for (Element e : doc.getElementsByTag("img")) {
            if (e.hasAttr("src")) {
                final String text = getValidPath(e.attr("src"));
                if (isNotEmpty(text)) return getValidPath(text);
            }
        }

        // etc empty
        return "";
    }

    public String getDomain(String url) {
        try {
            // 1st uri parse
            return new URI(url).getHost();
        } catch (Exception e) {
            // etc empty
            e.printStackTrace();
            return "";
        }
    }

    private String getValidPath(String url) {
        URI ogpUri=null;
        URI imgUri=null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        try {

            ogpUri = new URI(this.url);
            imgUri = ogpUri.resolve(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return imgUri.toString();

    }




    /*




     */


    private String getTitle(Document doc) {
        // <meta property="og:title" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:title");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the title : <meta property=\"og:title\" content=\"*\" />");
                return text;
            }
        }

        // <meta name="title" content="*">
        elements = doc.getElementsByAttributeValue("name", "title");
        if (elements.hasAttr("content")) {
            final String text = elements.attr("content");
            if (isNotEmpty(text)) {
                printLog("parse the title : <meta name=\"title\" content=\"*\">");
                return text;
            }
        }

        // <title>*</title>
        String title = doc.title();
        if (isNotEmpty(title)) {
            printLog("parse the title : <title>*</title>");
            return title;
        }

        // return empty
        printLog("parse the title fail");
        return "제목이 이 존재하지 않습니다.";
    }

    private String getDescription(Document doc) {
        // <meta property="og:description" content="*" />
        Elements elements = doc.getElementsByAttributeValue("property", "og:description");
        String text="상세 설명이 존재하지 않습니다.";
        if (elements.hasAttr("content")) {
            try {
                text = elements.attr("content");
                if (isNotEmpty(text)) {
                    printLog("parse the description : <meta property=\"og:description\" content=\"*\" />");
                    return text;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // <meta name="description" content="*">
        elements = doc.getElementsByAttributeValue("name", "description");
        if (elements.hasAttr("content")) {
            try {
                text = elements.attr("content");
                if (isNotEmpty(text)) {
                    printLog("parse the description : <meta name=\"description\" content=\"*\">");
                    return text;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // <p>*</p>
        for (Element e : doc.getElementsByTag("p")) {
            if (e.hasText() && isNotEmpty(e.text())) {
                try {
                    printLog("parse the description : <p>*</p>");
                    text=e.text();
                    return text;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        // <div>*</div>
        for (Element e : doc.getElementsByTag("div")) {
            if (e.hasText() && isNotEmpty(e.text())) {
                try {
                    printLog("parse the description : <div>*</div>");
                    text=e.text();
                    return text;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        // return empty
        printLog("parse the description fail");
        return text;
    }


    /*






     */

    private String _readHtmlSource(String url) throws IOException {
        // read byte source
        ByteArrayOutputStream source = _readsByteSource(url, true);
        printLog("html reading is successful");

        // parse charset
        String charset = _parseCharset(source);
        printLog("charset parsing is successful : " + charset);

        // encoding and return
        return source.toString(charset);
    }


    private ByteArrayOutputStream _readsByteSource(String url, boolean isRecursive) throws IOException {
        // print the log
        printLog("html reads the source : " + url);

        // url connection settings
        URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(timeoutMillis);
        connection.setRequestProperty("User-agent", "");
        connection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());

        // reads the byte source
        int b;
        InputStream is = connection.getInputStream();
        ByteArrayOutputStream source = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
            source.write(b);
        }
        is.close();

        // not called recursive function
        if (!isRecursive) {
            return source;
        }

        // html parse
        Document document = Jsoup.parse(source.toString());

        // if have <meta property="og:url" content="*" />, it will call recursive
        for (Element e : document.getElementsByAttributeValue("property", "og:url")) {
            if (e.hasAttr("content")) {
                final String text = e.attr("content").trim();
                if (isNotEmpty(text) && !url.equals(text)) {
                    printLog("html reads again the source : <meta property=\"og:url\" content=\"*\" />");
                    return _readsByteSource(getValidPath(text), true);
                }
            }
        }

        // if have <frame src="*"></frame>, it will call recursive
        for (Element e : document.getElementsByTag("frame")) {
            if (e.hasAttr("src")) {
                final String text = e.attr("src");
                if (isNotEmpty(text)) {
                    printLog("html reads again the source : <frame src=\"*\"></frame>");
                    return _readsByteSource(getValidPath(text), false);
                }
            }
        }

        // else, return byte source
        return source;
    }


    private String _parseCharset(ByteArrayOutputStream source) {
        // html parse
        Document document = Jsoup.parse(source.toString());

        // <meta http-equiv="content-type" content="charset=*" />
        for (Element e : document.getElementsByAttributeValue("http-equiv", "content-type")) {
            if (e.hasAttr("content")) {
                final String text = e.attr("content");
                if (isNotEmpty(text)) {
                    for (String str : text.split(Pattern.quote(";"))) {
                        if (str.contains("charset=")) {
                            final String charset = str.trim().replaceAll(Pattern.quote("charset="), "");
                            if (isNotEmpty(charset)) {
                                printLog("charset parse : <meta http-equiv=\"content-type\" content=\"charset=*\" />");
                                return charset;
                            }
                        }
                    }
                }
            }
        }

        // <meta charset="*" />
        for (Element e : document.getElementsByAttribute("charset")) {
            final String charset = e.attr("charset");
            if (isNotEmpty(charset)) {
                printLog("charset parse : '<meta charset=\"*\" />'");
                return charset;
            }
        }

        // not found
        printLog("charset parse : not found.");
        return "utf-8";
    }


    private boolean isNotEmpty(String str) {
        // if trim string is not empty, return true
        return str != null && str.trim().length() != 0;
    }


    private void printLog(String msg) {
        // if have logger, print log
        if (logger != null) logger.log(tag, msg);
    }
}