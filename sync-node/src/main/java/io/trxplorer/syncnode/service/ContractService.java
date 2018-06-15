package io.trxplorer.syncnode.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.Wallet;
import org.tron.protos.Contract.AccountCreateContract;
import org.tron.protos.Contract.AccountUpdateContract;
import org.tron.protos.Contract.AssetIssueContract;
import org.tron.protos.Contract.DeployContract;
import org.tron.protos.Contract.FreezeBalanceContract;
import org.tron.protos.Contract.ParticipateAssetIssueContract;
import org.tron.protos.Contract.TransferAssetContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.UnfreezeBalanceContract;
import org.tron.protos.Contract.VoteAssetContract;
import org.tron.protos.Contract.VoteWitnessContract;
import org.tron.protos.Contract.VoteWitnessContract.Vote;
import org.tron.protos.Contract.WithdrawBalanceContract;
import org.tron.protos.Contract.WitnessCreateContract;
import org.tron.protos.Contract.WitnessUpdateContract;
import org.tron.protos.Protocol.Transaction;
import org.tron.protos.Protocol.Transaction.Contract;
import org.tron.protos.Protocol.Transaction.Contract.ContractType;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import io.trxplorer.model.tables.records.ContractAccountCreateRecord;
import io.trxplorer.model.tables.records.ContractAccountUpdateRecord;
import io.trxplorer.model.tables.records.ContractAssetIssueRecord;
import io.trxplorer.model.tables.records.ContractDeployRecord;
import io.trxplorer.model.tables.records.ContractFreezeBalanceRecord;
import io.trxplorer.model.tables.records.ContractParticipateAssetIssueRecord;
import io.trxplorer.model.tables.records.ContractTransferAssetRecord;
import io.trxplorer.model.tables.records.ContractTransferRecord;
import io.trxplorer.model.tables.records.ContractUnfreezeBalanceRecord;
import io.trxplorer.model.tables.records.ContractVoteAssetRecord;
import io.trxplorer.model.tables.records.ContractVoteWitnessRecord;
import io.trxplorer.model.tables.records.ContractWithdrawBalanceRecord;
import io.trxplorer.model.tables.records.ContractWitnessCreateRecord;
import io.trxplorer.model.tables.records.ContractWitnessUpdateRecord;

public class ContractService {

	private DSLContext dslContext;

	
	@Inject
	public ContractService(DSLContext dslContext) {
		this.dslContext = dslContext;
	}

	public void importContracts(Transaction transaction, ULong txId) throws ServiceException{
		
		List<TableRecord<?>> contractRecords = new ArrayList<>();
		
		
		for (Contract contract : transaction.getRawData().getContractList()) {
			
			ByteString contractByteString = contract.getParameter().getValue();
			
			try {

				switch (contract.getTypeValue()) {
				case ContractType.AccountCreateContract_VALUE:

					ContractAccountCreateRecord accountCreateRecord = getCreateAccountCreateRecord(txId, AccountCreateContract.parseFrom(contractByteString));
					
					
					contractRecords.add(accountCreateRecord);

					break;
					
				case ContractType.AccountUpdateContract_VALUE:

					ContractAccountUpdateRecord accountUpdateRecord = getCreateAccountUpdateRecord(txId, AccountUpdateContract.parseFrom(contractByteString));
					
					
					contractRecords.add(accountUpdateRecord);

					break;
				case ContractType.AssetIssueContract_VALUE:
					
					ContractAssetIssueRecord assetIssueRecord = this.getAssetIssueContractRecord(txId,AssetIssueContract.parseFrom(contractByteString));
					
					
					contractRecords.add(assetIssueRecord);
					
					break;

				case ContractType.DeployContract_VALUE:
					
					ContractDeployRecord deployRecord = this.getDeployContractRecord(txId,DeployContract.parseFrom(contractByteString));
					
					
					contractRecords.add(deployRecord);
					
					break;
				case ContractType.ParticipateAssetIssueContract_VALUE:
					
					ContractParticipateAssetIssueRecord participateAssetIssueContract = this.getParticipateAssetIssueContractRecord(txId,ParticipateAssetIssueContract.parseFrom(contractByteString));
					
					
					contractRecords.add(participateAssetIssueContract);
					
					break;
				case ContractType.TransferContract_VALUE:
					
					ContractTransferRecord transferRecord = this.getTransferContractRecord(txId, TransferContract.parseFrom(contractByteString));
					
					
					contractRecords.add(transferRecord);
					
					break;
				case ContractType.TransferAssetContract_VALUE:

					ContractTransferAssetRecord transferAssetRecord = this.getTransferAssetContractRecord(txId,TransferAssetContract.parseFrom(contractByteString));

					
					contractRecords.add(transferAssetRecord);
					
					break;
				case ContractType.VoteAssetContract_VALUE:
					
					
					List<ContractVoteAssetRecord> voteAssetRecords = this.getVoteAssetContracts(txId,VoteAssetContract.parseFrom(contractByteString));
					
					contractRecords.addAll(voteAssetRecords);
					
					break;
				case ContractType.VoteWitnessContract_VALUE:
					
					List<ContractVoteWitnessRecord> voteWitnessRecords = this.getVoteWitnessContracts(txId,VoteWitnessContract.parseFrom(contractByteString));
					
					contractRecords.addAll(voteWitnessRecords);
					
					break;
				case ContractType.WitnessCreateContract_VALUE:

					ContractWitnessCreateRecord witnessCreateRecord = this.getWitnessCreateContractRecord(txId,WitnessCreateContract.parseFrom(contractByteString));
					
					
					contractRecords.add(witnessCreateRecord);
					
					break;
				case ContractType.WitnessUpdateContract_VALUE:

					ContractWitnessUpdateRecord witnessUpdateRecord = this.getWitnessUpdateContract(txId,WitnessUpdateContract.parseFrom(contractByteString));
					
					
					contractRecords.add(witnessUpdateRecord);
					
					break;
					
				case ContractType.FreezeBalanceContract_VALUE:

					ContractFreezeBalanceRecord freezeBalanceRecord = this.getFreezeBalanceContract(txId,FreezeBalanceContract.parseFrom(contractByteString));
					
					contractRecords.add(freezeBalanceRecord);
					
					break;
				case ContractType.UnfreezeBalanceContract_VALUE:

					ContractUnfreezeBalanceRecord unfreezeBalanceRecord = this.getUnFreezeBalanceContract(txId,UnfreezeBalanceContract.parseFrom(contractByteString));
					
					contractRecords.add(unfreezeBalanceRecord);
					
					break;
				case ContractType.WithdrawBalanceContract_VALUE:

					ContractWithdrawBalanceRecord widthdrawBalanceRecord = this.getWidthdrawBalanceContract(txId,WithdrawBalanceContract.parseFrom(contractByteString));
					
					contractRecords.add(widthdrawBalanceRecord);
					
					break;
				default:
					break;
				}

			} catch (InvalidProtocolBufferException e) {

				throw new ServiceException("Could not import contracts for transaction:"+Sha256Hash.of(transaction.getRawData().toByteArray()).toString(), e);
			}

		}
		
		this.dslContext.batchInsert(contractRecords).execute();

	}

	private ContractWithdrawBalanceRecord getWidthdrawBalanceContract(ULong txId, WithdrawBalanceContract contract) {
		
		ContractWithdrawBalanceRecord record = new ContractWithdrawBalanceRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractUnfreezeBalanceRecord getUnFreezeBalanceContract(ULong txId, UnfreezeBalanceContract unfreezeBalanceContract) {
		
		ContractUnfreezeBalanceRecord record = new ContractUnfreezeBalanceRecord();
		record.setOwnerAddress(Wallet.encode58Check(unfreezeBalanceContract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractFreezeBalanceRecord getFreezeBalanceContract(ULong txId, FreezeBalanceContract contract) {
		
		ContractFreezeBalanceRecord record = new ContractFreezeBalanceRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setFrozenBalance(ULong.valueOf(contract.getFrozenBalance()));
		record.setFrozenDuration(ULong.valueOf(contract.getFrozenDuration()));
		record.setTransactionId(txId);
		
		return record;
	}

	private ContractAccountUpdateRecord getCreateAccountUpdateRecord(ULong txId, AccountUpdateContract contract) {
		
		ContractAccountUpdateRecord record = new ContractAccountUpdateRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setAccountName(contract.getAccountName().toStringUtf8());
		record.setTransactionId(txId);
		
		return record;
	}


	private ContractWitnessUpdateRecord getWitnessUpdateContract(ULong txId, WitnessUpdateContract contract) {
		
		
		ContractWitnessUpdateRecord record = new ContractWitnessUpdateRecord();
		record.setUpdateUrl(contract.getUpdateUrl().toStringUtf8());
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);
		
		return record;
	}

	private ContractWitnessCreateRecord getWitnessCreateContractRecord(ULong txId, WitnessCreateContract contract) {
		
		ContractWitnessCreateRecord record = new ContractWitnessCreateRecord();
		record.setUrl(contract.getUrl().toStringUtf8());
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);
		
		return record;
	}

	private List<ContractVoteWitnessRecord> getVoteWitnessContracts(ULong txId, VoteWitnessContract contract) {
				
		List<ContractVoteWitnessRecord> votes= new ArrayList<>();
		
		String ownerAddress = Wallet.encode58Check(contract.getOwnerAddress().toByteArray());
		UByte support = contract.getSupport()==true ? UByte.valueOf((byte)1):UByte.valueOf((byte)0);
		
		for(Vote vote:contract.getVotesList()) {
			
			ContractVoteWitnessRecord voteRecord = new ContractVoteWitnessRecord();
			
			String voteAddress = Wallet.encode58Check(vote.getVoteAddress().toByteArray());

			voteRecord.setOwnerAddress(ownerAddress);
			voteRecord.setVoteAddress(voteAddress);
			voteRecord.setVoteCount(ULong.valueOf(vote.getVoteCount()));
			voteRecord.setSupport(support);
			voteRecord.setTransactionId(txId);
			
			
			votes.add(voteRecord);
		}

		return votes;
	}

	private List<ContractVoteAssetRecord> getVoteAssetContracts(ULong txId, VoteAssetContract contract) {
		
		
		
		String ownerAddress = Wallet.encode58Check(contract.getOwnerAddress().toByteArray());
		UByte support = contract.getSupport()==true ? UByte.valueOf((byte)1):UByte.valueOf((byte)0);
		UInteger count = UInteger.valueOf(contract.getCount());
		
		List<ContractVoteAssetRecord> votes= new ArrayList<>();
		
		for(ByteString voteAddress:contract.getVoteAddressList()) {
			
			String voteAddressB58 = Wallet.encode58Check(voteAddress.toByteArray());
			
			ContractVoteAssetRecord voteRecord = new ContractVoteAssetRecord();
			
			voteRecord.setOwnerAddress(ownerAddress);
			voteRecord.setSupport(support);
			voteRecord.setCount(count);
			voteRecord.setVoteToAddress(voteAddressB58);
			voteRecord.setTransactionId(txId);
			
			votes.add(voteRecord);
			
		}
		
		
		return votes;
	}

	private ContractTransferAssetRecord getTransferAssetContractRecord(ULong txId, TransferAssetContract contract) {
		
		ContractTransferAssetRecord record = new ContractTransferAssetRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setToAddress(Wallet.encode58Check(contract.getToAddress().toByteArray()));
		record.setAmount(ULong.valueOf(contract.getAmount()));
		record.setAssetName(contract.getAssetName().toStringUtf8());
		record.setTransactionId(txId);
		
		return record;
	}


	private ContractTransferRecord getTransferContractRecord(ULong txId, TransferContract contract) {
		
		ContractTransferRecord record = new ContractTransferRecord();
		
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setToAddress(Wallet.encode58Check(contract.getToAddress().toByteArray()));
		record.setAmount(ULong.valueOf(contract.getAmount()));
		record.setTransactionId(txId);
		

		return record;
	}
	
	private ContractParticipateAssetIssueRecord getParticipateAssetIssueContractRecord(ULong txId, ParticipateAssetIssueContract contract) {
		
		ContractParticipateAssetIssueRecord record = new ContractParticipateAssetIssueRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setToAddress(Wallet.encode58Check(contract.getToAddress().toByteArray()));
		record.setAssetName(contract.getAssetName().toStringUtf8());
		record.setAmount(ULong.valueOf(contract.getAmount()));
		record.setTransactionId(txId);
				
		return record;
	}

	private ContractDeployRecord getDeployContractRecord(ULong txId, DeployContract contract) {
		
		//FIXME: determine script type : binary or string ?
		
		ContractDeployRecord record = new ContractDeployRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setScript(contract.getScript().toStringUtf8());
		record.setTransactionId(txId);
		
		return record;
	}

	private ContractAssetIssueRecord  getAssetIssueContractRecord(ULong txId, AssetIssueContract contract) {

		ContractAssetIssueRecord record = new ContractAssetIssueRecord(); 
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setName(contract.getName().toStringUtf8());
		record.setTotalSupply(ULong.valueOf(contract.getTotalSupply()));
		record.setTrxNum(UInteger.valueOf(contract.getTrxNum()));
		record.setNum(UInteger.valueOf(contract.getNum()));
		record.setStartTime(new Timestamp(contract.getStartTime()));
		record.setEndTime(new Timestamp(contract.getEndTime()));
		record.setVoteScore(contract.getVoteScore());
		record.setDescription(contract.getDescription().toStringUtf8());
		record.setUrl(contract.getUrl().toStringUtf8());
		record.setTransactionId(txId);
		
		return record;
	}

	
	
	private ContractAccountCreateRecord getCreateAccountCreateRecord(ULong txId, AccountCreateContract contract) {
		
		ContractAccountCreateRecord record = new ContractAccountCreateRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);
		
		return record;
	}

}
