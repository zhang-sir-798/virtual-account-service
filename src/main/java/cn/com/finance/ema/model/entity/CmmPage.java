package cn.com.finance.ema.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 * @param <T>
 */
@Data
public class CmmPage<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int pageSize = 0;
    private int index = 0;
    private int pages = 0;
    private long total = 0;
    private List<T> datas = null;

}
