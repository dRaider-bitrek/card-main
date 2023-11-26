package com.example.cardclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public
class FileBlob {
    private String Status;
    private byte[] file;

    public FileBlob(String status) {
        Status = status;
    }
}
