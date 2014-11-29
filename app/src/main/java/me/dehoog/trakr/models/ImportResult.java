package me.dehoog.trakr.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author:  jordon
 * Created: November, 28, 2014
 * 5:00 PM
 */
public class ImportResult {

    private Message SCSMSG;

    private int status;
    private String status_message;

    public Message getSCSMSG() {
        return SCSMSG;
    }

    public int getStatus() {
        return status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public class Message {

        @SerializedName("SCS-MSG-LENGTH")
        private String length;
        @SerializedName("ACCOUNT-NUM")
        private String account_num;
        @SerializedName("BLN-GRP-TAG2")
        private Tag tag;

        public String getLength() {
            return length;
        }

        public String getAccount_num() {
            return account_num;
        }

        public Tag getTag() {
            return tag;
        }
    }

    public class Tag {

        @SerializedName("BLN-GRP-TAG2-ROW")
        List<Transaction> transactions;

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }

    public class Transaction {

        @SerializedName("BKI-KEY-LONG")
        private String key;
        @SerializedName("BKI-TXN-AMT")
        private String amount;
        @SerializedName("BKI-TXN-DESC")
        private String description;
        @SerializedName("BKI-TXN-DESC-EXPANDED")
        private String expanded;

        public String getKey() {
            return key;
        }

        public String getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public String getExpanded() {
            return expanded;
        }
    }

}
