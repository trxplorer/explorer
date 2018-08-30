package io.trxplorer.syncnode.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
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
import org.tron.protos.Contract.CreateSmartContract;
import org.tron.protos.Contract.ExchangeCreateContract;
import org.tron.protos.Contract.ExchangeInjectContract;
import org.tron.protos.Contract.ExchangeTransactionContract;
import org.tron.protos.Contract.ExchangeWithdrawContract;
import org.tron.protos.Contract.FreezeBalanceContract;
import org.tron.protos.Contract.ParticipateAssetIssueContract;
import org.tron.protos.Contract.ProposalApproveContract;
import org.tron.protos.Contract.ProposalCreateContract;
import org.tron.protos.Contract.ProposalDeleteContract;
import org.tron.protos.Contract.SetAccountIdContract;
import org.tron.protos.Contract.TransferAssetContract;
import org.tron.protos.Contract.TransferContract;
import org.tron.protos.Contract.TriggerSmartContract;
import org.tron.protos.Contract.UnfreezeAssetContract;
import org.tron.protos.Contract.UnfreezeBalanceContract;
import org.tron.protos.Contract.UpdateAssetContract;
import org.tron.protos.Contract.UpdateSettingContract;
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
import io.trxplorer.model.tables.records.ContractCreateSmartcontractRecord;
import io.trxplorer.model.tables.records.ContractExchangeCreateRecord;
import io.trxplorer.model.tables.records.ContractExchangeInjectRecord;
import io.trxplorer.model.tables.records.ContractExchangeTransactionContractRecord;
import io.trxplorer.model.tables.records.ContractExchangeWithdrawContractRecord;
import io.trxplorer.model.tables.records.ContractFreezeBalanceRecord;
import io.trxplorer.model.tables.records.ContractParticipateAssetIssueRecord;
import io.trxplorer.model.tables.records.ContractProposalApproveRecord;
import io.trxplorer.model.tables.records.ContractProposalCreateRecord;
import io.trxplorer.model.tables.records.ContractProposalDeleteRecord;
import io.trxplorer.model.tables.records.ContractSetAccountIdRecord;
import io.trxplorer.model.tables.records.ContractTransferAssetRecord;
import io.trxplorer.model.tables.records.ContractTransferRecord;
import io.trxplorer.model.tables.records.ContractTriggerSmartcontractRecord;
import io.trxplorer.model.tables.records.ContractUnfreezeAssetRecord;
import io.trxplorer.model.tables.records.ContractUnfreezeBalanceRecord;
import io.trxplorer.model.tables.records.ContractUpdateAssetRecord;
import io.trxplorer.model.tables.records.ContractUpdateSettingRecord;
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
				
				case ContractType.UpdateAssetContract_VALUE:
					
					ContractUpdateAssetRecord updateAssetContractRecord = this.getUpdateAssetContract(txId,UpdateAssetContract.parseFrom(contractByteString));
					
					contractRecords.add(updateAssetContractRecord);
					
					break;
					
				case ContractType.UnfreezeAssetContract_VALUE:
					
					ContractUnfreezeAssetRecord unfreezeAssetContractRecord = this.getUnfreezeAssetContract(txId,UnfreezeAssetContract.parseFrom(contractByteString));
					
					contractRecords.add(unfreezeAssetContractRecord);
					
					break;
					
				case ContractType.UpdateSettingContract_VALUE:
					
					ContractUpdateSettingRecord updateSettingContractRecord = this.getUpdateSettingContract(txId,UpdateSettingContract.parseFrom(contractByteString));
					
					contractRecords.add(updateSettingContractRecord);
					
					break;					
					
				case ContractType.SetAccountIdContract_VALUE:
					
					ContractSetAccountIdRecord setAccountIdContractRecord = this.getSetAccountIdContract(txId,SetAccountIdContract.parseFrom(contractByteString));
					
					contractRecords.add(setAccountIdContractRecord);
					
					break;					
				case ContractType.ProposalCreateContract_VALUE:
					
					ContractProposalCreateRecord proposalCreateContractRecord = this.getProposalCreateContract(txId,ProposalCreateContract.parseFrom(contractByteString));
					
					contractRecords.add(proposalCreateContractRecord);
					
					break;
				case ContractType.ProposalApproveContract_VALUE:
					
					ContractProposalApproveRecord proposalApproveRecord = this.getProposalApproveContract(txId,ProposalApproveContract.parseFrom(contractByteString));
					
					contractRecords.add(proposalApproveRecord);
					
					break;
				case ContractType.ProposalDeleteContract_VALUE:
					
					ContractProposalDeleteRecord proposalDeleteRecord = this.getProposalDeleteContract(txId,ProposalDeleteContract.parseFrom(contractByteString));
					
					contractRecords.add(proposalDeleteRecord);
					
					break;					
				case ContractType.CreateSmartContract_VALUE:
					
					ContractCreateSmartcontractRecord createSmartContractRecord = this.getCreateSmartContract(txId,CreateSmartContract.parseFrom(contractByteString));
					
					contractRecords.add(createSmartContractRecord);
					
					break;
				case ContractType.TriggerSmartContract_VALUE:
					
					ContractTriggerSmartcontractRecord triggerSmartContractRecord = this.getTriggerSmartContract(txId,TriggerSmartContract.parseFrom(contractByteString));
					
					contractRecords.add(triggerSmartContractRecord);
					
					break;
				case ContractType.ExchangeCreateContract_VALUE:
					
					ContractExchangeCreateRecord exchangeCreateRecord = this.getExchangeCreateContract(txId,ExchangeCreateContract.parseFrom(contractByteString));
					
					contractRecords.add(exchangeCreateRecord);
					
					break;
				case ContractType.ExchangeInjectContract_VALUE:
					
					ContractExchangeInjectRecord exchangeInjectRecord = this.getExchangeInjectContract(txId,ExchangeInjectContract.parseFrom(contractByteString));
					
					contractRecords.add(exchangeInjectRecord);
					
					break;
				case ContractType.ExchangeWithdrawContract_VALUE:
					
					ContractExchangeWithdrawContractRecord exchangeWithdrawRecord = this.getExchangeWidthdrawContract(txId,ExchangeWithdrawContract.parseFrom(contractByteString));
					
					contractRecords.add(exchangeWithdrawRecord);
					
					break;
				case ContractType.ExchangeTransactionContract_VALUE:
					
					ContractExchangeTransactionContractRecord exchangeTransactionContractRecord = this.getExchangeTransactionContract(txId,ExchangeTransactionContract.parseFrom(contractByteString));
					
					contractRecords.add(exchangeTransactionContractRecord);
					
					break;
				default:
					throw new ServiceException("Unknown contract type");
					
				}

			} catch (InvalidProtocolBufferException e) {

				throw new ServiceException("Could not import contracts for transaction:"+Sha256Hash.of(transaction.getRawData().toByteArray()).toString(), e);
			}

		}
		
		this.dslContext.batchInsert(contractRecords).execute();

	}

	private ContractExchangeTransactionContractRecord getExchangeTransactionContract(ULong txId,
			ExchangeTransactionContract contract) {
		ContractExchangeTransactionContractRecord record = new ContractExchangeTransactionContractRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractExchangeWithdrawContractRecord getExchangeWidthdrawContract(ULong txId,
			ExchangeWithdrawContract contract) {
		ContractExchangeWithdrawContractRecord record = new ContractExchangeWithdrawContractRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractExchangeInjectRecord getExchangeInjectContract(ULong txId, ExchangeInjectContract contract) {
		ContractExchangeInjectRecord record = new ContractExchangeInjectRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractExchangeCreateRecord getExchangeCreateContract(ULong txId, ExchangeCreateContract contract) {
		ContractExchangeCreateRecord record = new ContractExchangeCreateRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractTriggerSmartcontractRecord getTriggerSmartContract(ULong txId, TriggerSmartContract contract) {
		ContractTriggerSmartcontractRecord record = new ContractTriggerSmartcontractRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractCreateSmartcontractRecord getCreateSmartContract(ULong txId, CreateSmartContract contract) {
		ContractCreateSmartcontractRecord record = new ContractCreateSmartcontractRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractProposalDeleteRecord getProposalDeleteContract(ULong txId, ProposalDeleteContract contract) {
		ContractProposalDeleteRecord record = new ContractProposalDeleteRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractProposalApproveRecord getProposalApproveContract(ULong txId, ProposalApproveContract contract) {
		ContractProposalApproveRecord record = new ContractProposalApproveRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractProposalCreateRecord getProposalCreateContract(ULong txId, ProposalCreateContract contract) {
		ContractProposalCreateRecord record = new ContractProposalCreateRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractSetAccountIdRecord getSetAccountIdContract(ULong txId, SetAccountIdContract contract) {
		ContractSetAccountIdRecord record = new ContractSetAccountIdRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractUpdateSettingRecord getUpdateSettingContract(ULong txId, UpdateSettingContract contract) {
		ContractUpdateSettingRecord record = new ContractUpdateSettingRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractUnfreezeAssetRecord getUnfreezeAssetContract(ULong txId, UnfreezeAssetContract contract) {
		ContractUnfreezeAssetRecord record = new ContractUnfreezeAssetRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
	}

	private ContractUpdateAssetRecord getUpdateAssetContract(ULong txId, UpdateAssetContract contract) {
		ContractUpdateAssetRecord record = new ContractUpdateAssetRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setTransactionId(txId);		
		
		return record;
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
/*
	private ContractDeployRecord getSmartContractRecord(ULong txId, SmartContract contract) {
		
		//FIXME: determine script type : binary or string ?
		
		ContractDeployRecord record = new ContractDeployRecord();
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setScript(contract.getScript().toStringUtf8());
		record.setTransactionId(txId);
		
		return record;
	}
*/
	private ContractAssetIssueRecord  getAssetIssueContractRecord(ULong txId, AssetIssueContract contract) {
		
		ContractAssetIssueRecord record = new ContractAssetIssueRecord(); 
		record.setOwnerAddress(Wallet.encode58Check(contract.getOwnerAddress().toByteArray()));
		record.setName(contract.getName().toStringUtf8());
		record.setAbbr(contract.getAbbr().toStringUtf8());
		record.setTotalSupply(ULong.valueOf(contract.getTotalSupply()));
		record.setTrxNum(UInteger.valueOf(contract.getTrxNum()));
		record.setNum(UInteger.valueOf(contract.getNum()));
		record.setStartTime(Timestamp.valueOf(Instant.ofEpochMilli(contract.getStartTime()).atOffset(ZoneOffset.UTC).toLocalDateTime()));
		record.setEndTime(Timestamp.valueOf(Instant.ofEpochMilli(contract.getEndTime()).atOffset(ZoneOffset.UTC).toLocalDateTime()));
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
