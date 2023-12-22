package com.shop.common.utils.all.file;

public class FilesUtils {

    public static String extractFileExtensionName(String fileName) {
        String fileExtension = extractFileExtension(fileName);
        if (!fileExtension.isEmpty()) {
            return fileExtension.substring(1);
        }
        return "";
    }

    public static String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    public static String cropFileName(String fullPathToFile) {
        if (fullPathToFile == null) return "";
        if (fullPathToFile.contains("/") || fullPathToFile.contains("\\")) {
            int lastBackSlashPosition = fullPathToFile.lastIndexOf("/");
            int lastForwardSlashPosition = fullPathToFile.lastIndexOf("\\");
            int lastSlashPosition = Math.max(lastBackSlashPosition, lastForwardSlashPosition);
            return fullPathToFile.substring(0, lastSlashPosition);
        }
        return fullPathToFile;
    }

    public static String cropFilePath(String fullPathToFile) {
        if (fullPathToFile == null) return "";
        if (fullPathToFile.contains("/") || fullPathToFile.contains("\\")) {
            int lastBackSlashPosition = fullPathToFile.lastIndexOf("/");
            int lastForwardSlashPosition = fullPathToFile.lastIndexOf("\\");
            int lastSlashPosition = Math.max(lastBackSlashPosition, lastForwardSlashPosition);
            return fullPathToFile.substring(lastSlashPosition);
        }
        return fullPathToFile;
    }

}
