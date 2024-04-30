package cn.com.finance.ema.model.resp;

import cn.com.finance.ema.utils.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangsir
 * @Description: 分页显示结果
 * @date 2020/7/23
 */
@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = -4133717165046854344L;
    /**
     * 当前页数据
     */
    private List<T> result;
    /**
     * 当前页
     */
    private int currentPage = 1;
    /**
     * 页的条数
     */
    private int pageSize = 10;
    /**
     * 总条数
     */
    private int rows;
    /**
     * 总页数
     */
    private int allPages;

    // 得到总页数
    public int getAllPages() {
        if (this.allPages > 0) {
            return this.allPages;
        }
        int mod = this.rows % this.pageSize;
        if (mod == 0) {
            return this.rows / this.pageSize;
        }
        return this.rows / this.pageSize + 1;
    }

    private PageResult(List<T> result, int currentPage, int pageSize, int rows) {
        this.result = result == null ? new ArrayList<>() : result;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.rows = rows;
        this.allPages = getAllPages();
    }

    public static <T> PageResult<T> packagePageResult(List<T> resultList, Page<?> page) {
        return new PageResult<>(resultList, (int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
    }



    /**
     * 转换成目标类型的pageResult
     *
     * @param page  分页查询结果
     * @param clazz 目标类型
     * @return {@link PageResult}
     * @author zhangsir
     * @date 2020/7/23 16:26
     **/
    public static <T> PageResult<T> pageToResult(Page<?> page, Class<T> clazz) {
        final List<?> records = page.getRecords();
        if (clazz != null) {
            final List<T> targetList = CollectionUtil.listTypeChange(records, clazz);
            return packagePageResult(targetList, page);
        }
        return packagePageResult(null, page);
    }

    public static <T> PageResult<T> pageToResult(Page<?> page, List<T> list) {
        return packagePageResult(list, page);
    }

    public static <T> PageResult<T> pageToResult(Page<T> page) {
        final List<T> records = page.getRecords();
        return packagePageResult(records, page);
    }
}
