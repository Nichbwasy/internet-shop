package com.shop.common.utils.all.file;

public class FilesUtils {

    public static String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

}
