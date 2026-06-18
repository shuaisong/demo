package com.ysh.demo.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import cc.shinichi.library.ImagePreview
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.ysh.demo.bean.MultiFileBean
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File


class PictureSelectUtil private constructor() {
    companion object {
        /**
         * 选择图片
         * @param context Context
         * @param max Int
         * @param onResultCallbackListener OnResultCallbackListener<LocalMedia?>
         */
        fun getPicture(context: Context, max: Int,
                onResultCallbackListener: OnResultCallbackListener<LocalMedia>) {
            PictureSelector.create(context).openGallery(SelectMimeType.ofImage()).isDisplayCamera(
                true).setImageEngine(GlideEngine.createGlideEngine()).setMaxSelectNum(
                max).setSandboxFileEngine { context_, srcPath, mineType, call ->
                call?.onCallback(srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context_, srcPath, mineType))
            }.setCompressEngine(ImageFileCompressEngine()).forResult(onResultCallbackListener)
        }

        fun getVideo(context: Context, max: Int,
                onResultCallbackListener: OnResultCallbackListener<LocalMedia?>) {
            PictureSelector.create(context).openGallery(SelectMimeType.ofVideo()).isDisplayCamera(
                true).setImageEngine(GlideEngine.createGlideEngine()).setMaxSelectNum(
                max).setSandboxFileEngine { context_, srcPath, mineType, call ->
                call?.onCallback(srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context_, srcPath, mineType))
            }.setCompressEngine(ImageFileCompressEngine()).forResult(onResultCallbackListener)
        }
        fun getAll(context: Context, max: Int,
                onResultCallbackListener: OnResultCallbackListener<LocalMedia?>) {
            PictureSelector.create(context).openGallery(SelectMimeType.ofAll()).isDisplayCamera(
                true).setImageEngine(GlideEngine.createGlideEngine()).setMaxSelectNum(
                max).setSandboxFileEngine { context_, srcPath, mineType, call ->
                call?.onCallback(srcPath,
                    SandboxTransformUtils.copyPathToSandbox(context_, srcPath, mineType))
            }.setCompressEngine(ImageFileCompressEngine()).forResult(onResultCallbackListener)
        }
        //相机、文件权限
        fun hasWritePermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                return XXPermissions.isGranted(context, Permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Permission.CAMERA)
            }
            return XXPermissions.isGranted(context, Permission.WRITE_EXTERNAL_STORAGE,
                Permission.CAMERA)
        }

        //相机、文件权限
        fun requestWritePermission(context: Context, onPermissionCallback: OnPermissionCallback) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                XXPermissions.with(context).permission(Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.CAMERA).request(onPermissionCallback)
            } else {
                XXPermissions.with(context).permission(Permission.WRITE_EXTERNAL_STORAGE,
                    Permission.CAMERA).request(onPermissionCallback)
            }
        }

        //预览图片
        fun previewImg(localMedias: ArrayList<LocalMedia>, modelPosition: Int, context: Context) {
            val list = arrayListOf<String>()
            localMedias.forEach {
                list.add(it.path)
            }
            ImagePreview.instance.with(context as Activity).setIndex(modelPosition).setImageUrlList(
                list).setShowDownButton(false).setEnableDragClose(true).setShowCloseButton(
                true).start()
        }

        //预览图片
        fun previewImgUrl(urls: ArrayList<MultiFileBean>, modelPosition: Int, context: Context) {
            val list = arrayListOf<String>()
            urls.forEach {
                list.add(it.path)
            }
            ImagePreview.instance.with(context as Activity).setIndex(modelPosition).setImageUrlList(
                list).setShowDownButton(false).setEnableDragClose(true).setShowCloseButton(
                true).start();
        }

        //预览图片
        fun previewPhotoUrl(url: String, context: Context) {
            val list = arrayListOf(url)

            ImagePreview.instance.with(context as Activity).setIndex(0).setImageUrlList(
                list).setShowDownButton(false).setEnableDragClose(true).setShowCloseButton(
                true).start();
        }
    }

    //压缩引擎
    private class ImageFileCompressEngine : CompressFileEngine {
        override fun onStartCompress(context: Context?, source: ArrayList<Uri>,
                call: OnKeyValueResultCallbackListener?) {
            //鲁班压缩 512kb忽略
            Luban.with(context).load(source).ignoreBy(512).setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_") + postfix
            }.filter { path ->
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    true
                } else !PictureMimeType.isUrlHasGif(path)
            }.setCompressListener(object : OnNewCompressListener {
                override fun onStart() {}
                override fun onSuccess(source: String?, compressFile: File) {
                    call?.onCallback(source, compressFile.getAbsolutePath())
                }

                override fun onError(source: String, e: Throwable) {
                    call?.onCallback(source, null)
                }
            }).launch()
        }
    }

}