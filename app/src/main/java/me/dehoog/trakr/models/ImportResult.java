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

    public Message getSCSMSG() {
        return SCSMSG;
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

        List<Transaction> transactions;

        public List<Transaction> getTransactions() {
            return transactions;
        }
    }

    public class Transaction {

        @SerializedName("BKI-KEY-LONG")
        private long key;
        @SerializedName("BKI-TXN-AMT")
        private String amount;
        @SerializedName("BKI-TXN-DESC")
        private String description;
        @SerializedName("BKI-TXN-DESC-EXPANDED")
        private String expanded;

        public long getKey() {
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
