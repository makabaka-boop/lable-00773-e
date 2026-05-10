package com.finance.mapper;

import com.finance.entity.AccountBalance;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface AccountBalanceMapper {
    
    @Select("SELECT * FROM account_balance WHERE period = #{period} ORDER BY account_code")
    List<AccountBalance> findByPeriod(String period);
    
    @Select("SELECT * FROM account_balance WHERE period = #{period} ORDER BY account_code LIMIT #{size} OFFSET #{offset}")
    List<AccountBalance> findByPeriodPage(@Param("period") String period, @Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT COUNT(*) FROM account_balance WHERE period = #{period}")
    int countByPeriod(String period);
    
    @Select("SELECT * FROM account_balance WHERE account_code = #{accountCode} AND period = #{period}")
    AccountBalance findByAccountAndPeriod(@Param("accountCode") String accountCode, @Param("period") String period);
    
    @Insert("INSERT INTO account_balance (account_code, period, opening_debit, opening_credit, " +
            "current_debit, current_credit, closing_debit, closing_credit) " +
            "VALUES (#{accountCode}, #{period}, #{openingDebit}, #{openingCredit}, " +
            "#{currentDebit}, #{currentCredit}, #{closingDebit}, #{closingCredit})")
    int insert(AccountBalance balance);
    
    @Update("UPDATE account_balance SET current_debit=#{currentDebit}, current_credit=#{currentCredit}, " +
            "closing_debit=#{closingDebit}, closing_credit=#{closingCredit} " +
            "WHERE account_code=#{accountCode} AND period=#{period}")
    int update(AccountBalance balance);
}
