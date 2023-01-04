package com.batch.demo.demospringbatch.config;

import com.batch.demo.demospringbatch.entity.TransactionTbl;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TransactionTblRowMapper implements RowMapper<TransactionTbl> {
    public static final String ID_COLUMN = "ID";
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String AMOUNT = "AMOUNT";
    public static final String TRANSACTION_DATE = "TRANSACTION_DATE";
    public static final String IS_LIEN_RELEASED = "IS_LIEN_RELEASED";



    @Override
    public TransactionTbl mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionTbl tbl = new TransactionTbl();
        tbl.setId(rs.getInt(ID_COLUMN));
        tbl.setAmount(rs.getInt(AMOUNT));
        Date date = rs.getDate(TRANSACTION_DATE);
        Timestamp timestamp = new Timestamp(date.getTime());
        tbl.setTransactionDate(timestamp.toLocalDateTime());
        tbl.setLienReleased(rs.getBoolean(IS_LIEN_RELEASED));
        tbl.setAccountNumber(rs.getInt(ACCOUNT_NUMBER));
        tbl.setAccountType(rs.getString(ACCOUNT_TYPE));

        return tbl;
    }
}
