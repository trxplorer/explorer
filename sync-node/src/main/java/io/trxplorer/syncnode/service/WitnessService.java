package io.trxplorer.syncnode.service;

import static io.trxplorer.model.Tables.*;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.tron.core.Wallet;
import org.tron.protos.Protocol.Witness;

import com.google.inject.Inject;

import io.trxplorer.model.tables.records.WitnessRecord;
import io.trxplorer.troncli.TronFullNodeCli;

public class WitnessService {

	private DSLContext dslContext;
	private TronFullNodeCli tronFullNodeCli;
	private AccountService accountService;
	
	
	
	@Inject
	public WitnessService(DSLContext dslContext,TronFullNodeCli tronFullNodeCli,AccountService accountService) {
		this.dslContext = dslContext;
		this.tronFullNodeCli = tronFullNodeCli;
		this.accountService = accountService;
	}
	
	

	public void createOrUpdateWitness(Witness tronWitness) {
		
		String address = Wallet.encode58Check(tronWitness.getAddress().toByteArray());
		
		 WitnessRecord witnessRecord = this.dslContext.select(WITNESS.ID)
				.from(WITNESS).where(WITNESS.ADDRESS.eq(address)).fetchOneInto(WitnessRecord.class);
				
				if (witnessRecord==null) {
					
					this.dslContext.insertInto(WITNESS)
							.set(WITNESS.URL,tronWitness.getUrl())
							.set(WITNESS.TOTAL_PRODUCED,ULong.valueOf(tronWitness.getTotalProduced()))
							.set(WITNESS.TOTAL_MISSED,ULong.valueOf(tronWitness.getTotalMissed()))
							.set(WITNESS.LATEST_BLOCK_NUM,ULong.valueOf(tronWitness.getLatestBlockNum()))
							.set(WITNESS.LATEST_SLOT_NUM,tronWitness.getLatestSlotNum())
							.set(WITNESS.VOTE_COUNT,ULong.valueOf(tronWitness.getVoteCount()))
							.set(WITNESS.ISJOBS,tronWitness.getIsJobs()==true ? (byte)1 : (byte)0)
							.set(WITNESS.ADDRESS,address)
							.execute();
							
					
				}else {
					
					this.dslContext.update(WITNESS)
					.set(WITNESS.URL,tronWitness.getUrl())
					.set(WITNESS.TOTAL_PRODUCED,ULong.valueOf(tronWitness.getTotalProduced()))
					.set(WITNESS.TOTAL_MISSED,ULong.valueOf(tronWitness.getTotalMissed()))
					.set(WITNESS.LATEST_BLOCK_NUM,ULong.valueOf(tronWitness.getLatestBlockNum()))
					.set(WITNESS.LATEST_SLOT_NUM,tronWitness.getLatestSlotNum())
					.set(WITNESS.VOTE_COUNT,ULong.valueOf(tronWitness.getVoteCount()))
					.set(WITNESS.ISJOBS,tronWitness.getIsJobs()==true ? (byte)1 : (byte)0)
					.where(WITNESS.ID.eq(witnessRecord.getId()))
					.execute();
					
				}
	}

	public WitnessRecord create(WitnessRecord genesisWitnessRecord) {
		
		genesisWitnessRecord.attach(this.dslContext.configuration());
		genesisWitnessRecord.store();
		
		return genesisWitnessRecord;
	}
	
	
	public void updateMissingAccountId() {
		
		this.dslContext.update(WITNESS).set(WITNESS.ACCOUNT_ID,DSL.select(ACCOUNT.ID).from(ACCOUNT).where(WITNESS.ADDRESS.eq(ACCOUNT.ADDRESS))).execute();
		
	}
	
	public void removeWitnessNotInList(List<String> addresses) {
		this.dslContext.delete(WITNESS).where(WITNESS.ADDRESS.notIn(addresses)).execute();
	}
	
}
