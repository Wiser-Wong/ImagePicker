# ImagePicker
图片选择器

## 截图
![images](https://github.com/Wiser-Wong/ImagePicker/blob/master/images/image_picker1.gif)
![images](https://github.com/Wiser-Wong/ImagePicker/blob/master/images/image_picker2.gif)
![images](https://github.com/Wiser-Wong/ImagePicker/blob/master/images/image_picker3.gif)
![images](https://github.com/Wiser-Wong/ImagePicker/blob/master/images/image_picker4.gif)
![images](https://github.com/Wiser-Wong/ImagePicker/blob/master/images/image_picker5.gif)

## 环境配置
     allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
        }
      }

        dependencies {
              implementation 'com.github.Wiser-Wong:ImagePicker:1.1.0'
      }
## 使用控件
### 注意事项
  PhotoGridView 展示图片Layout 必须添加photoResId 图片id。

  * PhotoGridView
  
        <com.wiser.photo.grid.PhotoGridView
             android:id="@+id/gv_photo"
             android:layout_width="match_parent"
             android:layout_height="wrap_content"
             app:pgv_addLayoutId="@layout/add"
             app:pgv_addMode="end"
             app:pgv_isPreview="true"
             app:pgv_maxCounts="5"
             app:pgv_photoDeleteResId="@+id/iv_photo_delete"
             app:pgv_photoLayoutId="@layout/item"
             app:pgv_photoResId="@+id/iv_photo"
             app:pgv_selectPhotoMode="camera"
             app:pgv_selectPhotoSpanCount="3"
             app:pgv_spanCount="4"
             app:pgv_isCompress="true"
             app:pgv_isCameraCrop="true"
             app:pgv_compressQuality="90"
             app:pgv_compressWidth="480"
             app:pgv_compressHeight="800"
             app:pgv_cameraCropWidth="500"
             app:pgv_cameraCropHeight="500" />
  
        由于PhotoGridView控件中点击添加按钮处理了跳转逻辑，所以想要自定义跳转逻辑的同学需要添加监听
        gvPhoto.setOnPhotoGridListener(new PhotoGridView.OnPhotoGridListener() {
              @Override
              public void onAddClick(View view, int position) {
                    //添加
              }

              @Override
              public void onItemClick(View view, int position) {
                    //图片点击
              }

              @Override
              public void onDeleteClick(View view, int position) {
                    //删除
              }
        });
        
        //单独跳转选择图片界面
        PhotoSelectActivity.intent(this, 9 //剩余选择图片数量, 4 //选择图片界面显示的列数 , PhotoConstant.CAMERA_MODE //图片选择器模式，PhotoConstant.CAMERA_MODEL需要拍照模式，PhotoConstant.PHOTO_MODE只展示图片模式);
        //跳转选择图片界面传递数据
        PhotoSelectActivity.intent(this, new PhotoSettingData(9 //剩余选择图片数量, 3 //选择图片界面显示的列数, PhotoConstant.CAMERA_MODE //图片选择器模式, compressPath //压缩图片路径, true //是否压缩, 90 //质量, 480 //压缩宽, 800 //压缩高, true //是否拍照裁剪, 400 //裁剪宽, 400 //裁剪高));

        //选择图片回传数据需要接收
        @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == PhotoConstant.SELECT_PHOTO) {
            if (data != null) gvPhoto.setPhotoData(data.getStringArrayListExtra(PhotoConstant.INTENT_SELECT_PHOTO_KEY));
          }
        }
  
## 操作手册

### PhotoGridView
* pgv_addLayoutId:添加布局id
* pgv_photoLayoutId:展示图片布局id
* pgv_spanCount:展示的列数
* pgv_maxCounts:最大展示的数量
* pgv_isPreview:是否点击图片预览
* pgv_selectPhotoSpanCount:选择图片界面展示列数
* pgv_addMode:添加布局模式及位置（head、end、other）
* pgv_selectPhotoMode:选择图片模式（camera、photo）
* pgv_photoResId:展示图片布局内部图片控件id
* pgv_photoDeleteResId:展示图片布局内部删除id
* pgv_isCompress:是否压缩图片
* pgv_isCameraCrop:是否裁剪拍照图片
* pgv_compressQuality:压缩质量
* pgv_compressWidth:压缩宽
* pgv_compressHeight:压缩高
* pgv_cameraCropWidth:拍照裁剪宽
* pgv_cameraCropHeight:拍照裁剪高

PhotoConstant.CAMERA_MODE 拍照模式
PhotoConstant.PHOTO_MODE 相册模式
        
