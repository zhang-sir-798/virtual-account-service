package cn.com.finance.ema.model.req.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class AmcModelDetailReq implements Serializable {

    private static final long serialVersionUID = 5142966785998050354L;

    /**
     * 模型id , 唯一
     */
    @NotBlank(message = "模型id不能为空")
    private String modelOid;

}
