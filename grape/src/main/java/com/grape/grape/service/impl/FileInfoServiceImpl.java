package com.grape.grape.service.impl;

import com.grape.grape.service.FileInfoService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.FileInfo;
import com.grape.grape.mapper.FileInfoMapper;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-08-31
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>  implements FileInfoService {

}
