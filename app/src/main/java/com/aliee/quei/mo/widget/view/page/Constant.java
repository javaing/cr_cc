package com.aliee.quei.mo.widget.view.page;

import android.support.annotation.StringDef;

import com.aliee.quei.mo.utils.FileUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by newbiechen on 17-4-16.
 */

public class Constant {

    //book type
    public static final String BOOK_TYPE_COMMENT = "normal";
    public static final String BOOK_TYPE_VOTE = "vote";
    //book state
    public static final String BOOK_STATE_NORMAL = "normal";
    public static final String BOOK_STATE_DISTILLATE = "distillate";
    //Book Date Convert Format
    public static final String FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_FILE_DATE = "yyyy-MM-dd";
    //RxBus
    public static final int MSG_SELECTOR = 1;

    public static String getBookCachePath(){
        return FileUtils.getCachePath()+ File.separator
                + "book_cache"+ File.separator;
    }

    public static String getBookRecordPath(){
        return  FileUtils.getCachePath() + File.separator
                + "book_record" + File.separator;
    }


    //BookType
    @StringDef({
            BookType.ALL,
            BookType.XHQH,
            BookType.WXXX,
            BookType.DSYN,
            BookType.LSJS,
            BookType.YXJJ,
            BookType.KHLY,
            BookType.CYJK,
            BookType.HMZC,
            BookType.XDYQ,
            BookType.GDYQ,
            BookType.HXYQ,
            BookType.DMTR
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BookType {
        String ALL = "all";

        String XHQH = "xhqh";

        String WXXX = "wxxx";

        String DSYN = "dsyn";

        String LSJS = "lsjs";

        String YXJJ = "yxjj";
        String KHLY = "khly";
        String CYJK = "cyjk";
        String HMZC = "hmzc";
        String XDYQ = "xdyq";
        String GDYQ = "gdyq";
        String HXYQ = "hxyq";
        String DMTR = "dmtr";
    }

}
