package com.ysh.demo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao interface MinioDao {
    @Insert
    fun insert(minioInfo: MinioInfo)

    @Delete
    fun delete(minioInfo: MinioInfo)

    @Update
    fun update(minioInfo: MinioInfo)

    @Query("select * from minio where file =:path;")
    fun getByFile(path: String?): MinioInfo?

    @Query("select * from minio where upload_id =:upload_id;")
    fun getByUploadId(upload_id: Int): MinioInfo?

    @Query("select * from minio where download_id =:download_id;")
    fun getByDownloadId(download_id: Int): MinioInfo?

    @Query("SELECT * FROM minio")
    fun getAll(): List<MinioInfo>

    @Query("DELETE FROM minio")
    fun deleteAll()

    @Query("DELETE FROM minio WHERE file =:path")
    fun deleteByFilePath(path: String)
}
