package cn.com.finance.ema.model.resp;

import java.util.List;

/**
 * 树节点接口
 *
 * @author zhangsir
 * @create 2021-11-30 16:31
 */
public abstract class TreeNode {

    public abstract String getOid();

    public abstract String getParentId();

    public abstract Integer getSort();

    public abstract void setChildren(List<? extends TreeNode> list);

}
