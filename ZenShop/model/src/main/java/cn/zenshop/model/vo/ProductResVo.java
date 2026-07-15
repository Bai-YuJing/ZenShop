package cn.zenshop.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProductResVo {
    private ProductIndexVo productIndexVo;
    private String rating;
    private String businessName;
    private String businessLogo;
    private Double businessRating;
}
