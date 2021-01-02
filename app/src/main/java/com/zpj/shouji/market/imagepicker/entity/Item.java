/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.shouji.market.imagepicker.entity;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.zpj.shouji.market.imagepicker.utils.PathUtils;

import java.io.File;

public class Item implements Parcelable {
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        @Nullable
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
    public final long id;
    public final String mimeType;
    public final Uri uri;
    public final long size;
    public final long duration; // only for video, in ms
    private boolean isGif;

    private Item(long id, String mimeType, long size, long duration) {
        this.id = id;
        this.mimeType = mimeType;
        isGif = TextUtils.equals("image/gif", mimeType);
        Uri contentUri;
        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
    }

    private Item(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        uri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
        duration = source.readLong();
    }

    public static Item valueOf(Cursor cursor) {
        return new Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeParcelable(uri, 0);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    public Uri getContentUri() {
        return uri;
    }

    public boolean isGif() {
        return isGif;
    }

    public String getPath(Context context) {
        return PathUtils.uriToPath(context, uri);
    }

    public File getFile(Context context) {
        return new File(getPath(context));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }

        Item other = (Item) obj;
        return id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType)
                    || (mimeType == null && other.mimeType == null))
                && (uri != null && uri.equals(other.uri)
                    || (uri == null && other.uri == null))
                && size == other.size
                && duration == other.duration;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode();
        }
        result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(size).hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        return result;
    }
}
