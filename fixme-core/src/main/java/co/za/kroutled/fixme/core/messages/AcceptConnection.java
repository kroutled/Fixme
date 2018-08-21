package co.za.kroutled.fixme.core.messages;

import co.za.kroutled.fixme.core.MD5hash;

public class AcceptConnection extends Fix {
    private int id;

    public AcceptConnection(String messageType, int marketId, int id)
    {
        super(messageType, marketId);
        this.id = id;
        setChecksum(MD5hash.idHash(String.valueOf(id)));
    }

    public AcceptConnection() {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setNewChecksum ()
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
