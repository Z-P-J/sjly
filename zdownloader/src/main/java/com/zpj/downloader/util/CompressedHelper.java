/*
 * CompressedHelper.java
 *
 * Copyright (C) 2017-2019 Emmanuel Messulam<emmanuelbendavid@gmail.com>,
 * Raymond Lai <airwave209gt@gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zpj.downloader.util;

/**
 * @author Emmanuel
 *         on 23/11/2017, at 17:46.
 */

public class CompressedHelper {

    /**
     * Path separator used by all Decompressors and Extractors.
     * e.g. rar internally uses '\' but is converted to "/" for the app.
     */
    public static final char SEPARATOR_CHAR = '/';
    public static final String SEPARATOR = String.valueOf(SEPARATOR_CHAR).intern();

    public static final String fileExtensionZip = "zip", fileExtensionJar = "jar", fileExtensionApk = "apk";
    public static final String fileExtensionTar = "tar";
    public static final String fileExtensionGzipTarLong = "tar.gz", fileExtensionGzipTarShort = "tgz";
    public static final String fileExtensionBzip2TarLong = "tar.bz2", fileExtensionBzip2TarShort = "tbz";
    public static final String fileExtensionRar = "rar";
    public static final String fileExtension7zip = "7z";
    public static final String fileExtensionLzma = "tar.lzma";
    public static final String fileExtensionXz = "tar.xz";


    public static boolean isFileExtractable(String path) {
        String type = getExtension(path);

        return isZip(type) || isTar(type) || isRar(type) || isGzippedTar(type) || is7zip(type) || isBzippedTar(type) || isXzippedTar(type) || isLzippedTar(type);
    }

    public static final boolean isEntryPathValid(String entryPath){
        return !entryPath.startsWith("..\\") && !entryPath.startsWith("../") && !entryPath.equals("..");
    }

    private static boolean isZip(String type) {
        return type.endsWith(fileExtensionZip) || type.endsWith(fileExtensionJar)
                || type.endsWith(fileExtensionApk);
    }

    private static boolean isTar(String type) {
         return type.endsWith(fileExtensionTar);
    }

    private static boolean isGzippedTar(String type) {
         return type.endsWith(fileExtensionGzipTarLong) || type.endsWith(fileExtensionGzipTarShort);
    }

    private static boolean isBzippedTar(String type) {
        return type.endsWith(fileExtensionBzip2TarLong) || type.endsWith(fileExtensionBzip2TarShort);
    }

    private static boolean isRar(String type) {
        return type.endsWith(fileExtensionRar);
    }
    
    private static boolean is7zip(String type) {
        return type.endsWith(fileExtension7zip);
    }

    private static boolean isXzippedTar(String type) {
        return type.endsWith(fileExtensionXz);
    }

    private static boolean isLzippedTar(String type) {
        return type.endsWith(fileExtensionLzma);
    }

    private static String getExtension(String path) {
        return path.substring(path.indexOf('.')+1, path.length()).toLowerCase();
    }

}
