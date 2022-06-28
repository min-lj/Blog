package com.minzheng.blog.strategy.impl;

import com.minzheng.blog.config.CosConfigProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Lunatic
 * @since 2022/5/26 11:02
 */
@Service("cosUploadStrategyImpl")
@Slf4j
public class CosUploadStrategyImpl extends AbstractUploadStrategyImpl {
    private final CosConfigProperties cosConfigProperties;

    public CosUploadStrategyImpl(CosConfigProperties cosConfigProperties) {
        this.cosConfigProperties = cosConfigProperties;
    }

    @Override
    public Boolean exists(String filePath) {
        return getCosClient().doesObjectExist(cosConfigProperties.getBucketName(), filePath);
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) {

        // 生成cos客户端
        COSClient cosclient = getCosClient();

        try {

            // 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 设置输入流长度
            objectMetadata.setContentLength(inputStream.available());

            cosclient.putObject(cosConfigProperties.getBucketName(), path + fileName, inputStream, objectMetadata);

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            // 关闭客户端
            cosclient.shutdown();
        }


    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return cosConfigProperties.getUrl() + filePath;
    }

    public COSClient getCosClient() {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(cosConfigProperties.getSecretId(), cosConfigProperties.getSecretKey());
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(cosConfigProperties.getRegion()));
        // 3 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
}
