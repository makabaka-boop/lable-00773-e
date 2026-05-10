package com.finance.mapper;

import com.finance.entity.VoucherEntry;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VoucherEntryMapper {
    
    @Select("SELECT * FROM voucher_entry WHERE voucher_id = #{voucherId} ORDER BY seq")
    List<VoucherEntry> findByVoucherId(Long voucherId);
    
    @Insert("INSERT INTO voucher_entry (voucher_id, account_code, account_name, summary, debit_amount, credit_amount, seq) " +
            "VALUES (#{voucherId}, #{accountCode}, #{accountName}, #{summary}, #{debitAmount}, #{creditAmount}, #{seq})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VoucherEntry entry);
    
    int batchInsert(@Param("entries") List<VoucherEntry> entries);
    
    @Delete("DELETE FROM voucher_entry WHERE voucher_id = #{voucherId}")
    int deleteByVoucherId(Long voucherId);
    
    List<VoucherEntry> findByAccountAndPeriod(@Param("accountCode") String accountCode, 
                                               @Param("period") String period);
    
    List<VoucherEntry> findByAccountAndPeriodPage(@Param("accountCode") String accountCode, 
                                                   @Param("period") String period,
                                                   @Param("offset") int offset,
                                                   @Param("size") int size);
    
    int countByAccountAndPeriod(@Param("accountCode") String accountCode, 
                                @Param("period") String period);
}
