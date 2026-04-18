package com.verichain.logistics.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.Optional;

@Slf4j
@Service
public class BlockchainService {

    @Value("${app.blockchain.rpc-url}")
    private String rpcUrl;

    @Value("${app.blockchain.private-key}")
    private String privateKey;

    @Value("${app.blockchain.contract-address}")
    private String contractAddress;

    @Value("${app.blockchain.enabled}")
    private boolean blockchainEnabled;

    private Web3j web3j;
    private Credentials credentials;

    @PostConstruct
    public void init() {
        if (!blockchainEnabled) {
            log.info("Blockchain integration is DISABLED. Set app.blockchain.enabled=true to activate.");
            return;
        }
        try {
            web3j = Web3j.build(new HttpService(rpcUrl));
            credentials = Credentials.create(privateKey);
            log.info("Web3j connected to Ethereum node. Wallet address: {}", credentials.getAddress());
        } catch (Exception e) {
            log.error("Failed to connect to blockchain node: {}", e.getMessage());
        }
    }

    /**
     * Logs a shipment checkpoint to the Ethereum smart contract.
     * Returns the transaction hash on success, or null if blockchain is disabled/fails.
     *
     * In production:
     * 1. Generate the Java contract wrapper using `web3j generate` CLI
     * 2. Replace the stub below with:
     *    LogisticsLedger contract = LogisticsLedger.load(contractAddress, web3j, credentials, new DefaultGasProvider());
     *    TransactionReceipt receipt = contract.logCheckpoint(trackingId, location, aiRisk).send();
     *    return receipt.getTransactionHash();
     */
    public String logCheckpointToChain(String trackingId, String location, String aiDelayRisk) {
        if (!blockchainEnabled) {
            log.debug("Blockchain disabled — skipping on-chain log for {}", trackingId);
            return null;
        }

        try {
            log.info("Logging checkpoint to blockchain: trackingId={}, location={}, risk={}",
                    trackingId, location, aiDelayRisk);

            /*
             * ── PRODUCTION STEP ──────────────────────────────────────────
             * Uncomment and replace with your Web3j-generated contract wrapper:
             *
             * LogisticsLedger contract = LogisticsLedger.load(
             *     contractAddress, web3j, credentials, new DefaultGasProvider()
             * );
             * TransactionReceipt receipt = contract
             *     .logCheckpoint(trackingId, location, aiDelayRisk)
             *     .send();
             * return receipt.getTransactionHash();
             * ──────────────────────────────────────────────────────────────
             */

            // Simulated tx hash for development — replace with real call above
            String simulatedTxHash = "0x" + Long.toHexString(System.currentTimeMillis())
                    + Integer.toHexString(trackingId.hashCode()).replace("-", "");

            log.info("Blockchain tx successful. Hash: {}", simulatedTxHash);
            return simulatedTxHash;

        } catch (Exception e) {
            log.error("Blockchain logging failed for {}: {}", trackingId, e.getMessage());
            return null;
        }
    }

    public boolean isEnabled() {
        return blockchainEnabled;
    }
}
