package cn.com.finance.ema.mapper;


import cn.com.finance.ema.model.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 账户表 Mapper 接口
 * 可增加自定义sql，配合对应的mapperExpand.xml使用，不会随重新执行generator而更新
 * </p>
 *
 * @author zhang_sir
 * @since 2021-11-30
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    int addCash(@Param("acAmt") String acAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int subtractCash(@Param("acAmt") String acAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int addUncash(@Param("acAmt") String acAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int subtractUncash(@Param("acAmt") String acAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int opAcAfter(@Param("bAfter") String bAfter,@Param("fAfter") String fAfter, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int addCashWithFee(@Param("acAmt") String acAmt, @Param("extraAmt") String extraAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

    int subtractCashWithFee(@Param("acAmt") String acAmt, @Param("extraAmt") String extraAmt, @Param("lastDt") String lastDt, @Param("lastAcBalDataId") String lastAcBalDataId, @Param("acNo") String acNo);

}
