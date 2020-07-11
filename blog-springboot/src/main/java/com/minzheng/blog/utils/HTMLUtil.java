package com.minzheng.blog.utils;

/**
 * @author 11921
 */
public class HTMLUtil {

    /**
     * 删除文章内的markdown
     *
     * @param source 需要过滤的文本
     * @return 过滤后的内容
     */
    public static String deleteArticleTag(String source) {
        //删除HTML和markdown标签
        source = source.replaceAll("!\\[\\]\\((.*?)\\)", "").replaceAll("<[^>]+>", "");
        return deleteTag(source);
    }

    /**
     * 删除评论内容标签
     *
     * @param source 需要进行剔除HTML的文本
     * @return 过滤后的内容
     */
    public static String deleteCommentTag(String source) {
        //保留图片标签
        source = source.replaceAll("(?!<(img).*?>)<.*?>", "");
        return deleteTag(source);
    }

    /**
     * 删除标签
     *
     * @param source 文本
     * @return 过滤后的文本
     */
    private static String deleteTag(String source) {
        //删除转义字符
        source = source.replaceAll("&.{2,6}?;", "");
        //删除script标签
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");
        //删除style标签
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");
        return source;
    }

}
