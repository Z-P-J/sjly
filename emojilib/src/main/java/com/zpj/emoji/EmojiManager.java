package com.zpj.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EmojiManager {

    private static final String EMOT_DIR = "emoji/";

    private static final int CACHE_MAX_SIZE = 1024;
    private static Pattern mPattern;


    private static final List<String> CATEGORY_LIST = new ArrayList<>();
    private static final Map<String, List<Emoji>> CATEGORY_MAP = new HashMap<>();

    private static final List<Emoji> mDefaultEntries = new ArrayList<>();
//    private static final List<Entry> mQQEntries = new ArrayList<>();
    private static final Map<String, Emoji> mText2Entry = new HashMap<>();
    private static LruCache<String, Bitmap> mDrawableCache;

    public static void init(Context context) {
        load(context, EMOT_DIR + "emoji.xml");

        mPattern = makePattern();

        mDrawableCache = new LruCache<String, Bitmap>(CACHE_MAX_SIZE) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue != newValue)
                    oldValue.recycle();
            }
        };
    }

    public static int getCategoryCount() {
        return CATEGORY_LIST.size();
    }

    public static List<Emoji> getCategoryList(int index) {
        return CATEGORY_MAP.get(CATEGORY_LIST.get(index));
    }

    public static String getDisplayText(int categoryIndex, int index) {
        List<Emoji> list = getCategoryList(categoryIndex);
        return index >= 0 && index < list.size() ? list.get(index).text : null;
    }

    public static Drawable getDrawable(Context context, String text) {
        Emoji entry = mText2Entry.get(text);
        if (entry == null || TextUtils.isEmpty(entry.text)) {
            return null;
        }

        Bitmap cache = mDrawableCache.get(entry.assetPath);
        if (cache == null) {
            cache = loadAssetBitmap(context, entry.assetPath);
        }
        return new BitmapDrawable(context.getResources(), cache);
    }

    private static Bitmap loadAssetBitmap(Context context, String assetPath) {
        InputStream is = null;
        try {
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_HIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            if (bitmap != null) {
                mDrawableCache.put(assetPath, bitmap);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Pattern getPattern() {
        return mPattern;
    }

    private static Pattern makePattern() {
        return Pattern.compile(patternOfDefault());
    }

    private static String patternOfDefault() {
        return "\\[[^\\[]{1,10}\\]";
    }

    private static void load(Context context, String xmlPath) {
        new EntryLoader().load(context, xmlPath);
    }

    private static class EntryLoader extends DefaultHandler {
        private String catalog = "";

        void load(Context context, String assetPath) {
            InputStream is = null;
            try {
                is = context.getAssets().open(assetPath);
                Xml.parse(is, Xml.Encoding.UTF_8, this);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.equals("Catalog")) {
                catalog = attributes.getValue(uri, "Title");
                if (!CATEGORY_MAP.containsKey(catalog)) {
                    CATEGORY_MAP.put(catalog, new ArrayList<Emoji>());
                    CATEGORY_LIST.add(catalog);
                }
            } else if (localName.equals("Emoticon")) {
                String tag = attributes.getValue(uri, "Tag");
                String fileName = attributes.getValue(uri, "File");
                Emoji entry = new Emoji(tag, EMOT_DIR + catalog + "/" + fileName);

                mText2Entry.put(entry.text, entry);
                if (!"[\\超链接]".equals(tag)) {
                    List<Emoji> list = CATEGORY_MAP.get(catalog);
                    if (list == null) {
                        list = new ArrayList<>();
                        CATEGORY_MAP.put(catalog, list);
                    }
                    list.add(entry);
                }
            }
        }
    }

}
