package com.example.se_lab_23b02;

public class User {
    private long localId;
    private long globalId;

    public User(long localId)
    {
        this.localId = localId;
    }

    public long getLocalId()
    {
        return this.localId;
    }

    public long getGlobalId()
    {
        return this.globalId;
    }
}
