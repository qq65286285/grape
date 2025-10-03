package com.grape.grape.service.biz;


import com.grape.grape.model.Resp;
import com.grape.grape.service.CaseVersionsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CaseVersionBizServiceImpl implements CaseVersionBizService {


    @Resource
    CaseVersionsService caseVersionsService;

    @Override
    public Resp getListByCaseId(int caseId){
        return Resp.ok(caseVersionsService.listByCaseId(caseId));
    }
}
