package cn.zenshop.server.mapper;

import cn.zenshop.model.dto.BusinessPageDto;
import cn.zenshop.model.entity.Business;
import cn.zenshop.model.vo.BusinessPageVo;
import cn.zenshop.model.vo.BusinessVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessMapper extends BaseMapper<Business> {
    BusinessVo getBusinessById(String s);

    IPage<BusinessPageVo> adminGetBusinessPage(Page<BusinessVo> page, BusinessPageDto pageDto);

    IPage<BusinessPageVo> userGetBusinessPage(Page<BusinessVo> page, BusinessPageDto pageDto);
}
