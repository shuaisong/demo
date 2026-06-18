package com.ysh.demo.minio;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.util.Mimetypes;
import com.blankj.utilcode.util.LogUtils;
import com.ysh.demo.MainApplication;
import com.ysh.demo.base.ApiHelper;
import com.ysh.demo.db.AppDatabase;
import com.ysh.demo.db.MinioDao;
import com.ysh.demo.db.MinioInfo;
import com.ysh.demo.listener.RxObserver;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MinIoOssHelper {
    private static final String TAG = MinIoOssHelper.class.getSimpleName();

    private static MinIoOssHelper sMinIoOssHelper;

    private String ossUrl;

    private AmazonS3 amazonS3Client;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(6);


    public interface MinIoCallback {
        void progress(int progress);

        void onError(Throwable throwable);

        void onComplete(File file);

        //已经上传过
        void uploaded(File file);
    }

    private MinIoOssHelper() {

    }

    public static MinIoOssHelper getInstance() {
        if (sMinIoOssHelper == null) {
            sMinIoOssHelper = new MinIoOssHelper();
        }

        return sMinIoOssHelper;
    }

    public void init(String ossUrl, String accessKey, String secretKey) {
        this.ossUrl = ossUrl;
        try {
            amazonS3Client = new AmazonS3Client(new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return accessKey;//minio的key
                }

                @Override
                public String getAWSSecretKey() {
                    return secretKey;//minio的密钥
                }
            }, Region.getRegion(Regions.CN_NORTH_1), new ClientConfiguration().withMaxConnections(100).withConnectionTimeout(3000));
            S3ClientOptions clientOptions = S3ClientOptions.builder().setPathStyleAccess(true).build();
            amazonS3Client.setS3ClientOptions(clientOptions);
            amazonS3Client.setEndpoint(ossUrl);
        } catch (Exception e) {
            e.printStackTrace();
            amazonS3Client = null;
        }
    }

    public String getPath(@NotNull String objectName) {
        return ossUrl + "/" + ApiHelper.bucketName + "/" + objectName;
    }


    public void isExist(@NotNull String objectName, @NonNull RxObserver<Boolean> existsCallBack) {
        Observable.just(objectName).map(s -> amazonS3Client.doesObjectExist(ApiHelper.bucketName, objectName)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(existsCallBack);
    }

    public void uploadOssObject(String objectName, String filePath, MinIoCallback callback) {
        EXECUTOR.execute(() -> {
            try {
                File file = new File(filePath);
                AppDatabase database = AppDatabase.getDatabase();
                MinioDao minioDao = database.minioDao();
                MinioInfo minioInfo = minioDao.getByFile(filePath);
                TransferUtility sTransferUtility = TransferUtility.builder().s3Client(amazonS3Client).context(MainApplication.getInstance()).build();
                TransferObserver transferObserver = null;
                if (minioInfo == null) {
                    try {
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
                        transferObserver = sTransferUtility.upload(ApiHelper.bucketName, objectName, file, objectMetadata);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    minioInfo = createDbBean(transferObserver.getId(), ApiHelper.bucketName, objectName, file);
                    minioDao.insert(minioInfo);
                } else {
                    if (TransferState.COMPLETED.name().equals(minioInfo.state)) {
                        callback.progress(100);
                        callback.uploaded(file);
                        return;
                    }
                    if (minioInfo.bytesCurrent == minioInfo.bytesTotal) {
                        callback.progress(100);
                        callback.onComplete(file);
                        minioInfo.state = TransferState.COMPLETED.name();
                        minioDao.update(minioInfo);
                        return;
                    }
                    List<TransferObserver> transferObservers = sTransferUtility.getTransfersWithType(TransferType.UPLOAD);
                    for (TransferObserver observer : transferObservers) {
                        if (observer.getAbsoluteFilePath().equals(filePath)) {
                            transferObserver = observer;
                        }
                    }
                    if (transferObserver == null) {
                        LogUtils.d("新建transferObserver");
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
                        transferObserver = sTransferUtility.upload(ApiHelper.bucketName, objectName, file, objectMetadata);
                    }
                    int resId = transferObserver.getId();
                    TransferState transferState = transferObserver.getState();
                    LogUtils.d(TAG, "uploadOssObject: " + transferState.name() + ":" + resId);
                    if (transferState.equals(TransferState.FAILED)) {
                        boolean deleteTransferRecord = sTransferUtility.deleteTransferRecord(resId);
                        LogUtils.d(TAG, "upload: 失败了，重新开始" + resId + ":" + deleteTransferRecord);
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentType(Mimetypes.MIMETYPE_OCTET_STREAM);
                        transferObserver = sTransferUtility.upload(ApiHelper.bucketName, objectName, file, objectMetadata);
                    } else if (transferState.equals(TransferState.IN_PROGRESS)) {
                        if (sTransferUtility.pause(resId)) {
                            LogUtils.d(TAG, "upload: 进行中  暂停后重新开始");
                            transferObserver = sTransferUtility.resume(resId);
                        }
                    } else if (transferState.equals(TransferState.COMPLETED)) {
                        LogUtils.d(TAG, "upload: 已经完成了" + resId);
                        callback.uploaded(file);
                        return;
                    } else if (transferState.equals(TransferState.WAITING)) {
                        if (sTransferUtility.pause(resId)) {
                            LogUtils.d(TAG, "upload: 进行中  暂停后重新开始");
                            transferObserver = sTransferUtility.resume(resId);
                        }
                    } else {
                        transferObserver = sTransferUtility.resume(resId);
                        LogUtils.d(TAG, String.format(Locale.CHINA, "upload: 正在上传中无需重复 恢复:  total: %d, current: %d", transferObserver.getBytesTransferred(), transferObserver.getBytesTotal()));
                    }
                }

                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        LogUtils.d(id, state.name(), Thread.currentThread().getName());
                        new Thread(() -> {
                            MinioInfo minioInfo = minioDao.getByFile(filePath);
                            if (minioInfo != null) {
                                minioInfo.state = state.name();
                                minioInfo.uploadId = id;
                                minioDao.update(minioInfo);
                            }
                        }).start();
                        if (state.equals(TransferState.COMPLETED)) {
                            callback.onComplete(file);
                        } else if (state.equals(TransferState.FAILED)) {
                            callback.onError(new Exception("FAILED"));
                            sTransferUtility.cancel(id);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        if (bytesTotal == 0) return;
                        int process = (int) (bytesCurrent * 100 / bytesTotal);
                        LogUtils.d(id, process);
                        new Thread(() -> {
                            MinioInfo minioInfo = minioDao.getByFile(filePath);
                            if (minioInfo != null) {
                                minioInfo.bytesCurrent = bytesCurrent;
                                if (minioInfo.bytesCurrent == minioInfo.bytesTotal) {
                                    minioInfo.state = TransferState.COMPLETED.name();
                                }
                                minioDao.update(minioInfo);
                            }
                        }).start();
                        callback.progress(process);

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        new Thread(() -> {
                            MinioInfo minioInfo = minioDao.getByFile(filePath);
                            if (minioInfo != null) {
                                minioInfo.state = TransferState.FAILED.name();
                                minioDao.update(minioInfo);
                            }
                        }).start();
                        callback.onError(ex);
                        sTransferUtility.cancel(id);
                    }
                });
            } catch (Exception e) {
                callback.onError(e);
            }
        });


    }

    private MinioInfo createDbBean(int id, String bucketName, String objectName, File file) {
        MinioInfo minioInfo = new MinioInfo();
        minioInfo.uploadId = id;
        minioInfo.bucketName = bucketName;
        minioInfo.objectName = objectName;
        minioInfo.type = 0;
        minioInfo.state = TransferState.WAITING.name();
        minioInfo.bytesCurrent = 0;
        minioInfo.bytesTotal = file.length();
        minioInfo.file = file.getAbsolutePath();
        return minioInfo;
    }


    public interface ExistsCallBack {
        void onExists(boolean exists);
    }
}