package com.minzheng.blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件扩展名枚举
 *
 * @author yezhiqiu
 * @date 2021/08/12
 */
@Getter
@AllArgsConstructor
public enum FileExtEnum {
    /**
     * jpg文件
     */
    JPG(".jpg", "jpg文件"),
    /**
     * png文件
     */
    PNG(".png", "png文件"),
    /**
     * Jpeg文件
     */
    JPEG(".jpeg", "jpeg文件"),
    /**
     * wav文件
     */
    WAV(".wav", "wav文件");

    /**
     * 获取文件格式
     *
     * @param extName 扩展名
     * @return {@link FileExtEnum} 文件格式
     */
    public static FileExtEnum getFileExt(String extName) {
        for (FileExtEnum value : FileExtEnum.values()) {
            if (value.getExtName().equalsIgnoreCase(extName)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 扩展名
     */
    private final String extName;

    /**
     * 描述
     */
    private final String desc;

}
