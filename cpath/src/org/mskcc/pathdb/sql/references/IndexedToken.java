package org.mskcc.pathdb.sql.references;

public class IndexedToken {
    private String token;
    private int columnNumber;

    public IndexedToken (String token, int columnNumber) {
        this.token = token;
        this.columnNumber = columnNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String toString() {
        return "Token:  " + token + ", column number:  " + columnNumber;
    }
}
