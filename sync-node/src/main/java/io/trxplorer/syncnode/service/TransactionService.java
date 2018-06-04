package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.types.UByte;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Result;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.BlockRecord;
import io.trxplorer.model.tables.records.TransactionRecord;
import io.trxplorer.model.tables.records.TransactionResultRecord;

public class TransactionService {

	private DSLContext dslContext;
	private ContractService contractService;
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	@Inject
	public TransactionService(DSLContext dslContext, ContractService contractService) {
		this.dslContext = dslContext;
		this.contractService = contractService;
	}

	public void importTransaction(Transaction transaction, BlockRecord block) throws ServiceException {

		// create transaction
		
		 	Timestamp txTimestamp = new Timestamp(transaction.getRawData().getTimestamp());
		 	Timestamp txExpirationTimestamp = new Timestamp(transaction.getRawData().getExpiration());
		 	
		 	// FIXME: ugly patch because some transactions have some very weird strange (years with 5 digits ...)
		 	// Remove once problem gets corrected on tron side
		 	txTimestamp = ServiceHelper.isTimeStampValid(txTimestamp.toString()) ? txTimestamp : block.getTimestamp(); 
		 	txExpirationTimestamp = ServiceHelper.isTimeStampValid(txExpirationTimestamp.toString()) ? txExpirationTimestamp : null;		
		 	try {
				
		 		TransactionCapsule tc = new TransactionCapsule(transaction);

			TransactionRecord txRecord = this.dslContext.insertInto(TRANSACTION)
					.set(TRANSACTION.HASH, Sha256Hash.of(transaction.getRawData().toByteArray()).toString())
					.set(TRANSACTION.TIMESTAMP, txTimestamp)
					.set(TRANSACTION.EXPIRATION, txExpirationTimestamp)
					.set(TRANSACTION.BLOCK_ID, block.getId()).returning().fetchOne();

			// create results
			List<TransactionResultRecord> returnList = new ArrayList();

			for (Result txResult : transaction.getRetList()) {

				TransactionResultRecord resultRecord = new TransactionResultRecord();
				resultRecord.setTransactionId(txRecord.getId());
				resultRecord.setFee(ULong.valueOf(txResult.getFee()));
				resultRecord.setCode(UByte.valueOf(txResult.getRetValue()));

				returnList.add(resultRecord);
			}

			this.dslContext.batchInsert(returnList).execute();

			// create contracts
			this.contractService.importContracts(transaction, txRecord.getId());
			} catch (Exception e) {
				logger.error("block:"+block.getNum()+"-"+block.getWitnessAddress()+"============================>"+txTimestamp+"\n"+transaction);
			}		
	}

}
