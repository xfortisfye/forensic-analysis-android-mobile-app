package com.example.icarus;

import android.widget.TextView;

public class ExtMBR {
    private ExtPartition extPartition1;
    private ExtPartition extPartition2;
    private String signatureType;

    public ExtMBR() {
    }

    public void setExtPartition1(ExtPartition extPartition1) {
        this.extPartition1 = extPartition1;
    }

    public void setExtPartition2(ExtPartition extPartition2) {
        this.extPartition2 = extPartition2;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    public ExtPartition getExtPartition1() {
        return extPartition1;
    }

    public ExtPartition getExtPartition2() {
        return extPartition2;
    }

    public String getSignatureType() {
        return signatureType;
    }

    public Boolean chkExtMBRValidity(TextView testingText) {
        if (this.getSignatureType().equals("AA55")) {
            testingText.append("ExtMBR detected. Signature Type: " + this.getSignatureType() + "\n");
            return true;
        }
        else {
            testingText.append("Invalid ExtMBR. ExtMBR cannot be detected." + "\n");
            return false;
        }
    }


}
