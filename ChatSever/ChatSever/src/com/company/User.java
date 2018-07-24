package com.company;

import com.sun.istack.internal.NotNull;

public class User {
    String name;
    long timestamp;
    User(String input)
    {
        timestamp = System.currentTimeMillis();
        this.name = input;
    }
    public String nameOutPut(){
        return name;
    }
    public long timeStampOutPut() {
        return timestamp;
    }
    @Override
    public boolean equals(@NotNull Object obj) {
        return (obj instanceof User && ((User) obj).nameOutPut().equals(this.nameOutPut()))
                || obj == this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
