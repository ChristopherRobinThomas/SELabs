package com.example.se_lab_23b02;

public class User {
    private long localId;
    private long globalId;
    private String name;
    private String password;

    public User(long localId, String name, String password)
    {
        this.localId = localId;
        this.name = name;
        this.password = password;
    }

    public User(String s)
    {
        String[] values = s.split(",");
        this.localId = Integer.parseInt(values[0]);
        this.globalId = Integer.parseInt(values[1]);
        this.name = values[2];
        this.password = values[3];
    }

    public long getLocalId()
    {
        return this.localId;
    }

    public long getGlobalId()
    {
        return this.globalId;
    }

    public void setName(String name){ this.name = name; }
    public String getName(){ return this.name; }
    public String getPassword(){ return this.password; }

    public String ToString()
    {
        return String.valueOf(getLocalId()) + "," + String.valueOf(getGlobalId()) + "," + String.valueOf(getName()) + "," + String.valueOf(getPassword());
    }


    public boolean checkPassword(String pwd){return password.equals(pwd);}
}
