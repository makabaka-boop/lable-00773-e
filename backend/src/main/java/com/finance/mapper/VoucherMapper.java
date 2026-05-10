package com.finance.mapper;

import com.finance.entity.Voucher;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VoucherMapper {
    
    List<Voucher> findByCondition(@Param("period") String period, 
                                   @Param("status") String status,
                                   @Param("voucherNo") String voucherNo);
    
    List<Voucher> findByConditionPage(@Param("period") String period, 
                                       @Param("status") String status,
                                       @Param("voucherNo") String voucherNo,
                                       @Param("offset") int offset,
                                       @Param("size") int size);
    
    int countByCondition(@Param("period") String period, 
                         @Param("status") String status,
                         @Param("voucherNo") String voucherNo);
    
    @Select("SELECT * FROM voucher WHERE id = #{id}")
    Voucher findById(Long id);
    
    @Select("SELECT * FROM voucher WHERE voucher_no = #{voucherNo}")
    Voucher findByVoucherNo(String voucherNo);
    
    @Select("SELECT MAX(voucher_no) FROM voucher WHERE period = #{period}")
    String findMaxVoucherNo(String period);
    
    @Insert("INSERT INTO voucher (voucher_no, voucher_date, period, attachment_count, status, preparer) " +
            "VALUES (#{voucherNo}, #{voucherDate}, #{period}, #{attachmentCount}, #{status}, #{preparer})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Voucher voucher);
    
    @Update("UPDATE voucher SET voucher_date=#{voucherDate}, attachment_count=#{attachmentCount}, " +
            "status=#{status}, reviewer=#{reviewer}, update_time=CURRENT_TIMESTAMP WHERE id=#{id}")
    int update(Voucher voucher);
    
    @Delete("DELETE FROM voucher WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE voucher SET status='POSTED', reviewer=#{reviewer}, update_time=CURRENT_TIMESTAMP WHERE id=#{id}")
    int post(@Param("id") Long id, @Param("reviewer") String reviewer);
    
    @Update("UPDATE voucher SET status='VOID', update_time=CURRENT_TIMESTAMP WHERE id=#{id}")
    int voidVoucher(Long id);
}
