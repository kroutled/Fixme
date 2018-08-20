package co.za.kroutled.fixme.core.messages;

import co.za.kroutled.fixme.core.MD5hash;

public class acceptConnection extends Fix {
    private int id;

    acceptConnection(String messageType, int marketId, int id)
    {
        super(messageType, marketId);
        this.id = id;
        setChecksum(MD5hash.idHash(String.valueOf(id)));
    }

    public acceptConnection() {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setNewChecksum (String id)
    {
        setChecksum(MD5hash.idHash(String.valueOf(id)));
    }

    public String print()
    {
        return "MessageAcceptConnection {" +
                "ID = " + id +
                "|MSG_TYPE = '" + getMessageType() + "'" +
                "|CHECKSUM = '" + getChecksum() + "'" +
                '}';
    }
}
