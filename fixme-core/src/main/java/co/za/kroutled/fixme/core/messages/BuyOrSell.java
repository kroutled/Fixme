package co.za.kroutled.fixme.core.messages;

import co.za.kroutled.fixme.core.MD5hash;

public class BuyOrSell extends Fix {

    private String  messageAction;
    private int     actionLength;
    private int     id;
    private String  instrument;
    private int     instrumentLength;
    private int     quantity;
    private int     price;

    BuyOrSell(String messageType, int marketId, String messageAction, int id, String instrament, int quantity, int price)
    {
        super(messageType, marketId);
        this.messageAction = messageAction;
        this.id = id;
        this.instrument = instrament;
        this.quantity = quantity;
        this.price = price;
    }

    public BuyOrSell() {}

    public String getMessageAction()
    {
        return this.messageAction;
    }

    public void setMessageAction(String action)
    {
        this.messageAction = action;
        this.actionLength = action.length();
    }

    public int getActionLength()
    {
        return this.actionLength;
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getInstrument()
    {
        return this.instrument;
    }

    public void setInstrument(String instrament)
    {
        this.instrument = instrament;
        this.instrumentLength = instrament.length();
    }

    public int getInstrumentLength()
    {
        return this.instrumentLength;
    }

    public int getQuantity()
    {
        return this.quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public int getPrice()
    {
        return this.price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public String getMD5()
    {
        return MD5hash.idHash(String.valueOf(id).concat(instrument).concat(String.valueOf(quantity)).concat(messageAction));
    }

    public void setNewChecksum()
    {
        setChecksum(getMD5());
    }

    @Override
    public String toString() {
        return "MessageSellOrBuy {" +
                "ID = " + id +
                "|MSG_TYPE = '" + getMessageType() + "'" +
                "|MSG_ACTION = '" + messageAction + "'" +
                "|INSTRUMENT = '" + instrument + "'" +
                "|MARKET_ID = " + getMarketId() +
                "|QUANTITY = " + quantity +
                "|PRICE = " + price +
                "|CHECKSUM = '" + getChecksum() + "'" +
                '}';
    }
}
