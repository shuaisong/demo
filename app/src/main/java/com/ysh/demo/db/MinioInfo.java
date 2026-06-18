package com.ysh.demo.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "minio")
public class MinioInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "upload_id")
    public int uploadId;
    @ColumnInfo(name = "download_id")
    public int downloadId;

    /**
     * Transfer type, can be whether "upload 0" or "download 1"
     */
    @ColumnInfo(name = "type")
    public int type;

    /**
     * The current state of the transfer, values of all states are in
     * <code>TransferConstants</code>.
     */
    @ColumnInfo(name = "state")
    public String state;

    /**
     * The name of the bucket.
     */
    @ColumnInfo(name = "bucket_name")
    public String bucketName;
    /**
     * The name of the bucket.
     */
    @ColumnInfo(name = "object_name")
    public String objectName;

    @ColumnInfo(name = "bytes_total")
    public long bytesTotal = 0L;

    @ColumnInfo(name = "bytes_current")
    public long bytesCurrent = 0L;

    /**
     * The path of the file to transfer.
     */
    @ColumnInfo(name = "file")
    public String file;
}
