package cn.com.finance.ema.model.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *分页查询条件
 *
 * @date: 2021/05/14 17:48
 * @author: zhang_sir
 * @version: 1.0
 */
@Getter
@Setter
public abstract class PageReq implements Serializable {
    /**
     * 请求页码，默认第一页
     */
    private Integer currentPage = 1;
    /**
     * 每页条数，默认10条
     */
    private Integer pageSize = 10;

}
