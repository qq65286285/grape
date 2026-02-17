package com.grape.grape.controller;

import com.grape.grape.component.FileVo;
import com.grape.grape.component.MinioTemplate;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对外 API 控制器
 * 用于提供外部系统调用的接口
 */
@RestController
@RequestMapping("/public")
public class ApiController {

    @Autowired
    private MinioTemplate minioTemplate;

    /**
     * 上传文件到 Minio
     * @param file 要上传的文件
     * @param bucketName 存储桶名称
     * @return 上传结果，包含文件信息
     */
    @PostMapping("/upload")
    public Resp uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("bucketName") String bucketName) {
        if (file == null || file.isEmpty()) {
            return Resp.info(ResultEnumI18n.BUSINESS_ERROR.getCode(), "文件不能为空");
        }
        
        if (bucketName == null || bucketName.isEmpty()) {
            return Resp.info(ResultEnumI18n.BUSINESS_ERROR.getCode(), "存储桶名称不能为空");
        }
        
        // 验证存储桶名称是否符合 Amazon S3 标准
        if (!isValidBucketName(bucketName)) {
            return Resp.info(ResultEnumI18n.BUSINESS_ERROR.getCode(), "存储桶名称不符合标准，请使用小写字母、数字和连字符，长度3-63个字符，不能以连字符开头或结尾，不能包含连续连字符");
        }
        
        FileVo fileVo = minioTemplate.upload(file, bucketName);
        if (fileVo == null) {
            return Resp.info(ResultEnumI18n.BUSINESS_ERROR.getCode(), "上传失败");
        }
        
        return Resp.ok(fileVo);
    }
    
    /**
     * 验证存储桶名称是否符合 Amazon S3 标准
     * @param bucketName 存储桶名称
     * @return 是否有效
     */
    private boolean isValidBucketName(String bucketName) {
        if (bucketName == null || bucketName.isEmpty()) {
            return false;
        }
        // 存储桶名称长度必须在 3-63 个字符之间
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            return false;
        }
        // 存储桶名称只能包含小写字母、数字和连字符
        if (!bucketName.matches("^[a-z0-9][a-z0-9.-]*[a-z0-9]$") || bucketName.contains("..")) {
            return false;
        }
        // 存储桶名称不能以连字符开头或结尾
        if (bucketName.startsWith("-") || bucketName.endsWith("-")) {
            return false;
        }
        // 存储桶名称不能包含连续的连字符
        if (bucketName.contains("--")) {
            return false;
        }
        // 存储桶名称不能是 IP 地址格式
        if (bucketName.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$")) {
            return false;
        }
        return true;
    }
}
