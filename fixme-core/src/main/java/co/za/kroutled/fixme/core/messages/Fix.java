package co.za.kroutled.fixme.core.messages;

public class Fix {
    private int     typeLength;
    private String  messageType;
    private int     marketId;
    private int     checksumLength;
    private String  checksum;

    Fix(String messageType, int marketId)
    {
        this.messageType = messageType;
        this.typeLength = messageType.length();
        this.marketId = marketId;
    }

    public Fix(){}

    public String getMessageType()
    {
        return this.messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
        this.typeLength = messageType.length();
    }

    public int getMarketId()
    {
        return this.marketId;
    }

    public void setMarketId(int marketId)
    {
        this.marketId = marketId;
    }

    public String getChecksum()
    {
        return this.checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
        this.checksumLength = checksum.length();
    }

    public int getTypeLength() {
        return typeLength;
    }

    public int getChecksumLength() {
        return checksumLength;
    }
}
