package com.colin.bh.util;

import com.colin.bh.exception.NullFileException;

/**
 * 2024年06月07日16:19
 */
public class FileUtils {
    public static String getSuffixName(String originalFileName) throws NullFileException {
        if(originalFileName == null){
            throw new NullFileException("originalFileName is null");
        }
        int i = originalFileName.lastIndexOf(".");
        return originalFileName.substring(i);//.txt
    }

    /**
     * 获取拼接时间后的文件最终名称
     * @param originalFileName 用户上传时的原始名称
     * @return
     */
    public static String getTimestampFileName(String originalFileName, long currentTime) throws NullFileException {
        if(originalFileName == null){
            throw new NullFileException("originalFileName is null");
        }
        int i = originalFileName.lastIndexOf(".");
        String suffixName = originalFileName.substring(i);//.txt
        return originalFileName.substring(0, i) + "-" + currentTime + suffixName;
    }
}
