package com.example.icarus;

import android.widget.TextView;

public class ExtMBR {
    private ExtPartition extPartition;
    private String signatureType;

    public ExtMBR() {
    }

    public void setExtPartition(ExtPartition extPartition) {
        this.extPartition = extPartition;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    public ExtPartition getExtPartition() {
        return extPartition;
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
