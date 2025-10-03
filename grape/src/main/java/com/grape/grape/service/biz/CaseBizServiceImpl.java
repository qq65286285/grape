package com.grape.grape.service.biz;


import com.grape.grape.entity.CaseVersions;
import com.grape.grape.entity.Cases;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.service.CaseVersionsService;
import com.grape.grape.service.CasesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CaseBizServiceImpl implements CaseBizService {

    @Resource
    CasesService casesService;
    @Resource
    CaseVersionsService caseVersionsService;

    @Override
    public Resp updateCase(Cases cases){
        //当前版本做记录
        Cases caseOri = casesService.getById(cases.getId());
        if(null == caseOri){
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }

        CaseVersions caseVersions = new CaseVersions().getByCaseDao(caseOri);
        //入库用例版本，历史记录
        caseVersionsService.save(caseVersions);
        //更新用例
        cases.setVersion(caseOri.getVersion() + 1);
        casesService.updateById(cases);
        return Resp.info(ResultEnumI18n.SUCCESS);
    }
}
